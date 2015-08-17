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

import com.perforce.p4java.core.file.FileAction;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Encompasses all the information about an update to a file (edit, add, delete, integrate, move).
 */
public class P4FileUpdateState extends CachedState {
    @NotNull
    private final P4ClientFileMapping file;
    /** 0 for the default changelist, &lt; 0 for a locally stored changelist (no server side number), else a numbered changelist. */
    private int activeChangelist;
    @NotNull
    private FileAction action = FileAction.UNKNOWN;
    /** If this is an integrate, this reference sthe source of the integrate. */
    @Nullable
    private P4ClientFileMapping integrateSource;


    public P4FileUpdateState(@NotNull final P4ClientFileMapping file) {
        this.file = file;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass().equals(P4FileUpdateState.class)) {
            P4FileUpdateState that = (P4FileUpdateState) o;
            return that.file.equals(file);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    protected void serialize(@NotNull final Element wrapper, @NotNull final EncodeReferences refs) {
        wrapper.setAttribute("f", refs.getFileMappingId(file));
        wrapper.setAttribute("c", encodeLong(activeChangelist));
        wrapper.setAttribute("a", action.toString());
        if (integrateSource != null) {
            wrapper.setAttribute("s", refs.getFileMappingId(integrateSource));
        }
        serializeDate(wrapper);
    }

    @Nullable
    protected static P4FileUpdateState deserialize(@NotNull final Element wrapper,
            @NotNull final DecodeReferences refs) {
        P4ClientFileMapping file = refs.getFileMapping(getAttribute(wrapper, "f"));
        if (file == null) {
            return null;
        }
        P4FileUpdateState ret = new P4FileUpdateState(file);
        ret.deserializeDate(wrapper);
        Long r = decodeLong(getAttribute(wrapper, "r"));
        Long c = decodeLong(getAttribute(wrapper, "c"));
        ret.activeChangelist = (c == null) ? -1 : c.intValue();
        ret.action = FileAction.UNKNOWN;
        String a = getAttribute(wrapper, "a");
        if (a != null) {
            ret.action = FileAction.fromString(a);
        }
        ret.integrateSource = refs.getFileMapping(getAttribute(wrapper, "s"));
        return ret;
    }
}
