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
package demoapp.dom.mixins.legacy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import lombok.RequiredArgsConstructor;

import demoapp.dom.mixins.DemoItem;

@Mixin(method="prop")
@RequiredArgsConstructor
public class MixinLegacyDemo_mixedInProperty {
    
    @SuppressWarnings("unused")
    private final MixinLegacyDemo holder;
    
    @Action(semantics=SemanticsOf.SAFE)   // required
    @ActionLayout(contributed=Contributed.AS_ASSOCIATION)  // required
    public DemoItem prop() {
        return DemoItem.of("A mixed-in Property", null);
    }
    
}
 