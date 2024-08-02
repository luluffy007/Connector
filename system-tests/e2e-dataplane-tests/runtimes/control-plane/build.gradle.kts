/*
 *  Copyright (c) 2024 Dawex Systems
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Dawex Systems - initial configuration
 */

plugins {
    `java-library`
}

dependencies {
    implementation(project(":core:control-plane:control-plane-core"))
    implementation(project(":extensions:control-plane:transfer:transfer-data-plane-signaling"))
    implementation(project(":extensions:data-plane-selector:data-plane-selector-client"))
    implementation(project(":extensions:common:iam:iam-mock"))
    implementation(project(":extensions:common:http"))
}

edcBuild {
    publish.set(false)
}
