package org.nakedobjects.object;

import org.nakedobjects.container.configuration.ConfigurationFactory;
import org.nakedobjects.object.control.Hint;
import org.nakedobjects.object.defaults.FastFinder;
import org.nakedobjects.object.defaults.InternalNakedObject;
import org.nakedobjects.object.reflect.PojoAdapter;
import org.nakedobjects.object.reflect.internal.InternalAbout;

import org.apache.log4j.Logger;


public class NakedClass implements InternalNakedObject {
    private final static Logger LOG = Logger.getLogger(NakedClass.class);
    private final String className;
    private NakedObjectSpecification specification;
    private boolean createPersistentInstances;

    {
        createPersistentInstances = ConfigurationFactory.getConfiguration().getBoolean("nakedclass.create-persistent", true);
    }

    public NakedClass(String name) {
        specification = NakedObjectSpecificationLoader.getInstance().loadSpecification(name);
        className = name;
    }

    //    public NakedClass() {}

    public void aboutActionFind(InternalAbout about) {
        about.setDescription("Get a simple finder object to start searches within the " + getSingularName() + " instances");
        about.setName("Find " + getPluralName());
        about.unusableOnCondition(!getObjectManager().hasInstances(forObjectType()), "No instances available to find");
        Hint ca = specification.getClassAbout();
        if (ca != null && ca.canAccess().isVetoed()) {
            about.invisible();
        }
    }

    public void aboutActionInstances(InternalAbout about) {
        about.setDescription("Get the " + getSingularName() + " instances");
        about.setName(getPluralName());
        Hint ca = specification.getClassAbout();
        if (ca != null && ca.canAccess().isVetoed()) {
            about.invisible();
        } else {
            about.unusableOnCondition(!getObjectManager().hasInstances(forObjectType()), "No instances available");
        }
    }

    public String getPluralName() {
        return forObjectType().getPluralName();
    }

    public String getSingularName() {
        return forObjectType().getSingularName();
    }

    public String getFullName() {
        return forObjectType().getFullName();
    }

    public void aboutActionNewInstance(InternalAbout about) {
        about.setDescription("Create a new " + getSingularName() + " instance");
        about.setName("New " + getSingularName());
        Hint ca = specification.getClassAbout();
        if (ca != null && ca.canUse().isVetoed()) {
            about.invisible();
        }
    }

    public FastFinder actionFind() {
        FastFinder find = new FastFinder();
        find.setObjectManager(getObjectManager());
        find.setFromClass(forObjectType());
        return find;
    }

    public NakedCollection actionInstances() {
        return allInstances();
    }

    public NakedCollection allInstances() {
        return getObjectManager().allInstances(forObjectType(), false);
    }

    public NakedObject actionNewInstance() {
        return newInstance();
    }

    public NakedObject newInstance() {
        NakedObjectManager objectManager = getObjectManager();

        if (createPersistentInstances) {
            objectManager.startTransaction();
        }

        NakedObject object = objectManager.createTransientInstance(forObjectType());
        //        object = (NakedObject) forNakedClass().acquireInstance();
        object.setContext(NakedObjectContext.getDefaultContext());
        //        object.created();

        object.setResolved();
        if (createPersistentInstances) {
            try {
                getObjectManager().makePersistent(object);
                objectManager.endTransaction();
            } catch (NotPersistableException e) {
                object = PojoAdapter.createAdapter(getObjectManager().generatorError("Failed to create instance of " + this, e));
                LOG.error("Failed to create instance of " + this, e);
            }
        }

        return object;
    }

    public String getName() {
        return className;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();

        s.append("NakedClass");
        s.append(" [");
        /*
         * // type of object - EO, Primitive, Collection, with Status etc //
         * Persistent/transient & Resolved or not s.append(isPersistent() ? "P" :
         * (isFinder() ? "F" : "T")); s.append(isResolved() ? "R" : "-");
         *  // obect identifier if (getOid() != null) { s.append(":");
         * s.append(getOid().toString().toUpperCase()); } else { s.append(":-"); }
         */

        // title
        s.append(' ');
        s.append(className);

        s.append("]");

        s.append("  " + Long.toHexString(super.hashCode()).toUpperCase());

        return s.toString();
    }

    public NakedObjectSpecification forObjectType() {
        if (specification == null) {
            if (getName().length() == 0) {
                throw new NakedObjectRuntimeException();
            }
            specification = NakedObjectSpecificationLoader.getInstance().loadSpecification(getName());
        }
        return specification;
    }

    private NakedObjectManager getObjectManager() {
        return NakedObjectContext.getDefaultContext().getObjectManager();
    }
}

/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/