/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomee.catalina.routing;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;

public class RouterValve extends ValveBase {
    private SimpleRouter router = new SimpleRouter();

    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        final String destination = router.route(request.getRequestURI());
        if (destination == null) {
            getNext().invoke(request, response);
            return;
        }

        response.sendRedirect(destination);
    }

    public void setConfigurationPath(URL configurationPath) {
        router.readConfiguration(configurationPath);
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        router.JMXOn("Router Valve " + System.identityHashCode(this));
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        router.cleanUp();
        super.stopInternal();
    }
}
