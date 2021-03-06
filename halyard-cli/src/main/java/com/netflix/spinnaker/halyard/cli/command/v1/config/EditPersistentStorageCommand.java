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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.netflix.spinnaker.halyard.cli.services.v1.Daemon;
import com.netflix.spinnaker.halyard.cli.ui.v1.AnsiUi;
import com.netflix.spinnaker.halyard.config.model.v1.node.PersistentStorage;
import lombok.AccessLevel;
import lombok.Getter;

@Parameters()
public class EditPersistentStorageCommand extends AbstractConfigCommand {
  @Getter(AccessLevel.PUBLIC)
  private String commandName = "edit-storage";

  @Getter(AccessLevel.PUBLIC)
  private String description = "Configure Spinnaker's persistent storage options.";

  @Parameter(
      names = "--account-name",
      description = "The Spinnaker account that has access to either a GCS or S3 bucket. This account "
          + "does _not_ have to be separate from the accounts used to manage/deploy infrastructure, but "
          + "it can be."
  )
  private String accountName;

  @Parameter(
      names = "--bucket",
      description = "The name of a storage bucket that your specified account has access to. If not "
          + "specified, a random name will be chosen. If you specify a globally unique bucket name "
          + "that doesn't exist yet, Halyard will create that bucket for you."
  )
  private String bucket;

  @Parameter(
      names = "--root-folder",
      description = "The root folder in the chosen bucket to place all of Spinnaker's persistent data in."
  )
  private String rootFolder = "spinnaker";

  @Override
  protected void executeThis() {
    String currentDeployment = getCurrentDeployment();

    PersistentStorage persistentStorage = Daemon.getPersistentStorage(currentDeployment, !noValidate);

    persistentStorage.setAccountName(isSet(accountName) ? accountName : persistentStorage.getAccountName());
    persistentStorage.setBucket(isSet(bucket) ? bucket : persistentStorage.getBucket());
    persistentStorage.setRootFolder(isSet(rootFolder) ? rootFolder : persistentStorage.getRootFolder());

    Daemon.setPersistentStorage(currentDeployment, !noValidate, persistentStorage);

    AnsiUi.success("Successfully updated persistent storage configuration.");
  }
}
