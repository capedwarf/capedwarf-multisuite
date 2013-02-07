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

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.capedwarf.multisuite.MultiContext;
import org.jboss.capedwarf.multisuite.MultiProvider;
import org.jboss.capedwarf.multisuite.scan.ScanMultiProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.capedwarf.common.support.All;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MultiDeployment {
    @Deployment
    public static WebArchive getDeployment() throws Exception {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "capedwarf-tests.war");
        ClassLoader cl = MultiDeployment.class.getClassLoader();
        URL arqXml = cl.getResource("arquillian.xml");
        if (arqXml == null) {
            throw new IllegalArgumentException("No arquillian.xml?!");
        }
        File root = new File(arqXml.toURI()).getParentFile();

        MultiContext mc = new MultiContext(war, root, cl);

        // Common
        mc.addClass(All.class);

        MultiProvider provider = new ScanMultiProvider();
        provider.provide(mc);

        return war;
    }
}
