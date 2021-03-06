/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.cli.command.v1.config;

import com.beust.jcommander.Parameters;
import com.netflix.spinnaker.halyard.cli.services.v1.Daemon;
import com.netflix.spinnaker.halyard.cli.ui.v1.AnsiUi;
import com.netflix.spinnaker.halyard.config.model.v1.node.Features;
import lombok.AccessLevel;
import lombok.Getter;

@Parameters()
public class GetFeaturesCommand extends AbstractConfigCommand {
  @Getter(AccessLevel.PUBLIC)
  private String commandName = "get-features";

  @Getter(AccessLevel.PUBLIC)
  private String description = "Display the state of Spinnaker's feature flags.";

  @Override
  protected void executeThis() {
    String currentDeployment = getCurrentDeployment();

    Features features = Daemon.getFeatures(currentDeployment, !noValidate);

    AnsiUi.success("Features: ");
    AnsiUi.raw(features.toString());
  }
}
