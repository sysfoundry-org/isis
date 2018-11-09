/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.apache.isis.core.runtime.system.context.IsisContext;

/**
 * Responsibility: Display 'Showing all of 123' at the bottom of data tables.
 * <p>
 * Implementation Note: this is almost a copy of {@link NoRecordsToolbar}
 * 
 * @since 2.0.0-M2
 */
public class IsisTotalRecordsToolbar extends AbstractToolbar {

    private static final long serialVersionUID = 1L;

    public IsisTotalRecordsToolbar(final DataTable<?, ?> table) {
        
        this(table, new Model<String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                
                final boolean isPrototyping = IsisContext.getEnvironment()
                        .getDeploymentCategory().isPrototyping();
                
                if(!isPrototyping) {
                    return String.format("Showing all of %d", table.getRowCount());
                }

                // when prototyping append a 'took seconds message' ...
                
                return String.format("Showing all of %d %s", 
                        table.getRowCount(), 
                        PrototypingMessageProvider.getTookTimingMessage());
            }
            
        });
        
    }
    
    /**
     * @param table
     *            data table this toolbar will be attached to
     * @param messageModel
     *            model that will be used to display the "total records" message
     */
    protected IsisTotalRecordsToolbar(final DataTable<?, ?> table, final IModel<String> messageModel) {
        super(table);

        WebMarkupContainer td = new WebMarkupContainer("td");
        add(td);

        td.add(AttributeModifier.replace("colspan", new IModel<String>()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject()
            {
                return String.valueOf(table.getColumns().size()).intern();
            }
        }));
        td.add(new Label("msg", messageModel));
    }

    /**
     * Only shows this toolbar when there is only one pages (when there is no page navigation)
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();

        if(getTable().getRowCount() == 0) {
            setVisible(false);
            return;
        }
        
        setVisible(getTable().getPageCount() == 1);
    }

}