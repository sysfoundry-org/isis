package org.nakedobjects.viewer.skylark.basic;

import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectContext;
import org.nakedobjects.object.NakedObjectManager;
import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.NakedObjectSpecificationLoader;
import org.nakedobjects.object.control.Hint;
import org.nakedobjects.object.reflect.PojoAdapter;
import org.nakedobjects.viewer.skylark.Bounds;
import org.nakedobjects.viewer.skylark.CompositeViewSpecification;
import org.nakedobjects.viewer.skylark.Content;
import org.nakedobjects.viewer.skylark.ContentDrag;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.MenuOption;
import org.nakedobjects.viewer.skylark.MenuOptionSet;
import org.nakedobjects.viewer.skylark.ObjectContent;
import org.nakedobjects.viewer.skylark.Padding;
import org.nakedobjects.viewer.skylark.Size;
import org.nakedobjects.viewer.skylark.View;
import org.nakedobjects.viewer.skylark.ViewAxis;
import org.nakedobjects.viewer.skylark.ViewDrag;
import org.nakedobjects.viewer.skylark.Workspace;
import org.nakedobjects.viewer.skylark.core.AbstractView;
import org.nakedobjects.viewer.skylark.core.CompositeObjectView;
import org.nakedobjects.viewer.skylark.util.ViewFactory;

import java.util.Vector;

import org.apache.log4j.Logger;


public class DefaultWorkspace extends CompositeObjectView implements Workspace {
    private static final Logger LOG = Logger.getLogger(AbstractView.class);
    protected Workspace newWorkspace;

    public DefaultWorkspace(Content content, CompositeViewSpecification specification, ViewAxis axis) {
        super(content, specification, axis);
    }

    public View addOpenViewFor(Naked object, Location at) {
        ViewFactory factory = ViewFactory.getViewFactory();
        View view = factory.createOpenRootView((NakedObject) object);
        view.setLocation(at);
        view.setSize(view.getRequiredSize());
        getWorkspace().addView(view);
        return view;
    }

    public void drop(ContentDrag drag) {
        getViewManager().showArrowCursor();

        NakedObject source = ((ObjectContent) drag.getSourceContent()).getObject();
 
        View newView;
        if (source.getObject() instanceof NakedClass && drag.isCtrl()) {
            NakedObjectSpecification spec = ((NakedClass) source).forObjectType();
            Hint classAbout = spec.getClassAbout();
            if(classAbout != null && classAbout.canUse().isVetoed()) {
                return;
            }

            LOG.info("new " + spec.getShortName() + " instance");
            newView = newInstance(spec, !drag.isCtrl());
        } else {
            if (drag.isShift()) {
                newView = ViewFactory.getViewFactory().createOpenRootView(source);
            } else {
                // place object onto desktop as icon
                //getSubviews();
                View icon = drag.getSource();
                if(! icon.getSpecification().isOpen() ) {
                    View[] subviews = getSubviews();
                    for (int i = 0; i < subviews.length; i++) {
                        if(subviews[i] ==icon) {
                            icon.markDamaged();
                            Location at = drag.getTargetLocation();
                            at.translate(drag.getOffset());
                            icon.setLocation(at);
                            icon.markDamaged();
                            return;
                        }
                    }
                }
                newView = ViewFactory.getViewFactory().createIconizedRootView(source);
            }
        }
        newView.setSize(newView.getRequiredSize());
        Location location = drag.getTargetLocation();
        location.add(40, -40);
        newView.setLocation(location);
        drag.getTargetView().addView(newView);
        
    }

    public void drop(ViewDrag drag) {
        getViewManager().showDefaultCursor();

        View view = drag.getSourceView();
        if (view.getSpecification().isSubView()) {
            if (view.getSpecification().isOpen()) {
                // TODO remove the open view from the container and place on workspace; replace the internal view with an icon
            } else {
                ObjectContent content = (ObjectContent) view.getContent();
                Location newLocation = drag.getViewDropLocation();
                addOpenViewFor(content.getObject(), newLocation);
            }
        } else {
            view.markDamaged();
            Location newLocation = drag.getViewDropLocation();
            view.setLocation(newLocation);
            view.limitBoundsWithin(getBounds());
            view.markDamaged();
        }
    }

    public Padding getPadding() {
        return new Padding();
    }

    public Workspace getWorkspace() {
        return this;
    }

    public void layout() {
        if(isLayoutInvalid()) {
            super.layout();
            
            View[] subviews = getSubviews();
            Bounds bounds = new Bounds(getSize());
            for (int i = 0; i < subviews.length; i++) {
                subviews[i].limitBoundsWithin(bounds);
            }
            
            super.layout();
        }
    }
    
    public void lower(View view) {
        if (views.contains(view)) {
            views.removeElement(view);
            views.insertElementAt(view, 0);
            markDamaged();
        }
    }

    private View newInstance(NakedObjectSpecification cls, boolean openAView) {
        NakedObject object =  getObject().getContext().getObjectManager().createInstance(cls);

        return openAView ? ViewFactory.getViewFactory().createOpenRootView(object) : ViewFactory.getViewFactory()
                .createIconizedRootView(object);
    }

    public void raise(View view) {
        if (views.contains(view)) {
            views.removeElement(view);
            views.addElement(view);
            markDamaged();
        }
    }

    public void removeView(View view) {
        view.markDamaged();
        super.removeView(view);
    }

    public void removeViewsFor(NakedObject object) {
        View views[] = getSubviews();

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            if (((ObjectContent) view.getContent()).getObject() == object) {
                view.dispose();
            }
        }
    }

    public String toString() {
        return "Workspace" + getId();
    }

    public void viewMenuOptions(MenuOptionSet options) {
        super.viewMenuOptions(options);

        getViewManager().menuOptions(options);

        options.add(MenuOptionSet.VIEW, new MenuOption("About...") {
            public void execute(Workspace workspace, View view, Location at) {
                AboutView aboutView = new AboutView();
                
                Size windowSize = aboutView.getRequiredSize();
                Size workspaceSize = getWorkspace().getSize();
                int x = workspaceSize.getWidth() / 2 - windowSize.getWidth() / 2;
                int y = workspaceSize.getHeight() / 2 - windowSize.getHeight() / 2;
                aboutView.setSize(windowSize);
                getWorkspace().addView(aboutView);
                aboutView.setLocation(new Location(x, y));
     //           limitBounds(aboutView);
            }
        });

        options.add(MenuOptionSet.OBJECT, new MenuOption("Naked Classes...") {
            public void execute(Workspace workspace, View view, Location at) {
                NakedObjectSpecification[] specs = NakedObjectSpecificationLoader.getInstance().getAllSpecifications();
                //ArbitraryCollectionVector classCollection = new ArbitraryCollectionVector("Naked Classes");
                Vector classCollection = new Vector();
                NakedObjectManager objectManager = NakedObjectContext.getDefaultContext().getObjectManager();
                for (int i = 0; i < specs.length; i++) {
                    NakedObjectSpecification cls = specs[i];
                    if(cls.isObject()) {
                        classCollection.addElement(PojoAdapter.createAdapter(objectManager.getNakedClass(cls)));
                    }
                }
                View classesView = ViewFactory.getViewFactory().createOpenRootView(PojoAdapter.createAdapter(classCollection));
                classesView.setLocation(at);
                addView(classesView);
            }
        });

        options.add(MenuOptionSet.VIEW, new MenuOption("Close all") {
            public void execute(Workspace workspace, View view, Location at) {
                View views[] = getSubviews();

                for (int i = 0; i < views.length; i++) {
                    View v = views[i];
                    if (v.getSpecification().isOpen()) {
                        v.dispose();
                    }
                }

                markDamaged();
            }
        });

        options.add(MenuOptionSet.VIEW, new MenuOption("Tidy up views") {
            public void execute(Workspace workspace, View view, Location at) {
                View views[] = getSubviews();

                for (int i = 0; i < views.length; i++) {
                    View v = views[i];
                    if (v.getSpecification().isOpen()) {
                        v.setLocation(WorkspaceBuilder.UNPLACED);
                    }
                }

                workspace.invalidateLayout();
                markDamaged();
            }
        });

        options.add(MenuOptionSet.VIEW, new MenuOption("Tidy up icons") {
            public void execute(Workspace workspace, View view, Location at) {
                View views[] = getSubviews();

                for (int i = 0; i < views.length; i++) {
                    View v = views[i];
                    if (!v.getSpecification().isOpen()) {
                        v.setLocation(WorkspaceBuilder.UNPLACED);
                    }
                }

                workspace.invalidateLayout();
                markDamaged();
            }
        });

        options.add(MenuOptionSet.VIEW, new MenuOption("Tidy up all") {
            public void execute(Workspace workspace, View view, Location at) {
                View views[] = getSubviews();

                for (int i = 0; i < views.length; i++) {
                    views[i].setLocation(WorkspaceBuilder.UNPLACED);
                }

                workspace.invalidateLayout();
                markDamaged();
            }
        });
    }
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2005 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */
