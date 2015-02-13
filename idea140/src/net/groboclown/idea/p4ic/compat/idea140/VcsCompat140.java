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

package net.groboclown.idea.p4ic.compat.idea140;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsUserRegistry;
import net.groboclown.idea.p4ic.compat.VcsCompat;
import org.jetbrains.annotations.Nullable;

public class VcsCompat140 extends VcsCompat {
    @Override
    public void setupPlugin(@Nullable Project project) {
        ServiceManager.getService(project, VcsUserRegistry.class); // make sure to read the registry before opening commit dialog
    }
}
