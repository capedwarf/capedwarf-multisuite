/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.test.capedwarf.multisuite.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.capedwarf.multisuite.TestItem;
import org.jboss.capedwarf.multisuite.TestItemParser;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.capedwarf.common.support.All;
import org.jboss.test.capedwarf.common.test.BaseTest;
import org.jboss.test.capedwarf.common.test.TestContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DeploymentsTestCase extends BaseTest {
    @Deployment
    public static WebArchive getDeployment() throws Exception {
        TestContext context = new TestContext("capedwarf-multisuite");
        WebArchive war = getCapedwarfDeployment(context);
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Common
        addClasses(classes, All.class);

        ClassLoader cl = DeploymentsTestCase.class.getClassLoader();

        // Tests
        String file = System.getProperty("tests.file", "tests.txt");
        List<TestItem> items = TestItemParser.parse(cl.getResourceAsStream(file));
        for (TestItem item : items) {
            if (TestItem.Type.CLASS == item.getType()) {
                Class<?> clazz = cl.loadClass(item.getFqn());
                addClasses(classes, clazz);
            } else {
                war.addPackage(item.getFqn());
            }
        }

        war.addClasses(classes.toArray(new Class[classes.size()]));

        return war;
    }

    protected static void addClasses(Set<Class<?>> classes, Class<?> current) {
        if (current == null || current == Object.class)
            return;

        classes.add(current);

        for (Class<?> iface : current.getInterfaces()) {
            addClasses(classes, iface);
        }

        addClasses(classes, current.getSuperclass());
    }
}
