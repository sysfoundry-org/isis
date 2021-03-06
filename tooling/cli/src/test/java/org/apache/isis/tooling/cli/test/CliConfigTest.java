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
package org.apache.isis.tooling.cli.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.isis.commons.internal.resources._Yaml;
import org.apache.isis.tooling.cli.CliConfig;

import lombok.val;

class CliConfigTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void loadConfigFromYaml() {
        val config = _Yaml.readYaml(CliConfig.class, this.getClass().getResourceAsStream("isis-tooling.yml"));
        assertConfigIsPopulated(config);
    }
    
    // -- HELPER

    private void assertConfigIsPopulated(CliConfig config) {
        assertNotNull(config);
        assertNotNull(config.getProjectDoc());
        assertEquals("These tables summarize all Maven artifacts available with _Apache Isis_.", config.getProjectDoc().getDescription());
        assertNotNull(config.getProjectDoc().getArtifactGroups());
        assertTrue(config.getProjectDoc().getArtifactGroups().size()>5);
        
        
        assertEquals(true, config.getProjectDoc().isFixOrphandedAdocIncludeStatements());
        assertEquals(3, config.getProjectDoc().getNamespacePartsSkipCount());
    }

}
