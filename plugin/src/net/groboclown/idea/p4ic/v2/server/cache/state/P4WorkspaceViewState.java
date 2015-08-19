/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.groboclown.idea.p4ic.v2.server.cache.state;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps a copy of the user's workspace view mappings (the root, alt roots, and view).  This is used to
 * detect when the cached files should be thrown out and reloaded.
 */
public class P4WorkspaceViewState extends CachedState {
    private final String name;
    private List<String> roots = new ArrayList<String>();
    private List<ViewMapping> depotWorkspaceMapping = new ArrayList<ViewMapping>();

    public static class ViewMapping {
        private final String depot;
        private final String client;

        public ViewMapping(final String depot, final String client) {
            this.depot = depot;
            this.client = client;
        }

        public String getDepot() {
            return depot;
        }

        public String getClient() {
            return client;
        }
    }

    public P4WorkspaceViewState(final String name) {
        this.name = name;
    }

    public void addRoot(@NotNull final String root) {
        roots.add(root);
    }

    public void addViewMapping(@NotNull final String depotSpec, @NotNull final String clientSpec) {
        depotWorkspaceMapping.add(new ViewMapping(depotSpec, clientSpec));
    }

    @Override
    protected void serialize(@NotNull final Element wrapper, @NotNull final EncodeReferences ref) {
        serializeDate(wrapper);
        wrapper.setAttribute("n", name);
        for (String root : roots) {
            Element el = new Element("r");
            wrapper.addContent(el);
            el.setAttribute("p", root);
        }
        for (ViewMapping entry : depotWorkspaceMapping) {
            Element el = new Element("m");
            wrapper.addContent(el);
            el.setAttribute("d", entry.getDepot());
            el.setAttribute("w", entry.getClient());
        }
    }

    @Nullable
    protected static P4WorkspaceViewState deserialize(@NotNull final Element wrapper,
            @NotNull final DecodeReferences refs) {
        String name = getAttribute(wrapper, "n");
        if (name == null) {
            return null;
        }
        P4WorkspaceViewState ret = new P4WorkspaceViewState(name);
        for (Element el : wrapper.getChildren("r")) {
            String root = getAttribute(el, "p");
            if (root != null) {
                ret.roots.add(root);
            }
        }
        for (Element el : wrapper.getChildren("m")) {
            String d = getAttribute(el, "d");
            String w = getAttribute(el, "w");
            if (d != null && w != null) {
                ret.depotWorkspaceMapping.add(new ViewMapping(d, w));
            }
        }
        return ret;
    }

}