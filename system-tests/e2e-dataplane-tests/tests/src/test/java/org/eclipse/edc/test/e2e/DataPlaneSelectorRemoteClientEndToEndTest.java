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
 *       Dawex Systems - initial implementation
 *
 */

package org.eclipse.edc.test.e2e;

import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClient;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClientFactory;
import org.eclipse.edc.connector.dataplane.selector.spi.instance.DataPlaneInstance;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.edc.junit.extensions.EmbeddedRuntime;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.eclipse.edc.junit.extensions.RuntimePerMethodExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.eclipse.edc.util.io.Ports.getFreePort;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EndToEndTest
class DataPlaneSelectorRemoteClientEndToEndTest {

    private static final int SELECTOR_CONTROL_PORT = getFreePort();

    private final DataPlaneClientFactory dataPlaneClientFactory = mock();

    @RegisterExtension
    private final RuntimeExtension selectorPlane = new RuntimePerMethodExtension(new EmbeddedRuntime(
            "selector-plane",
            Map.of(
                    "web.http.control.port", String.valueOf(SELECTOR_CONTROL_PORT),
                    "web.http.control.path", "/control"
            ),
            ":system-tests:e2e-dataplane-tests:runtimes:selector-plane"
    )).registerServiceMock(DataPlaneClientFactory.class, dataPlaneClientFactory);

    @RegisterExtension
    private final RuntimeExtension controlPlane = new RuntimePerMethodExtension(new EmbeddedRuntime(
            "control-plane",
            Map.of(
                    "edc.dpf.selector.url", String.format("http://localhost:%d/control/v1/dataplanes", SELECTOR_CONTROL_PORT)
            ),
            ":system-tests:e2e-dataplane-tests:runtimes:control-plane"
    )).registerServiceMock(ProtocolWebhook.class, mock());

    @Test
    void shouldSelectDataPlane() {
        DataPlaneClient dataPlaneClient = mock();
        when(dataPlaneClientFactory.createClient(any())).thenReturn(dataPlaneClient);
        when(dataPlaneClient.checkAvailability()).thenReturn(StatusResult.success());

        var selectorService = selectorPlane.getService(DataPlaneSelectorService.class);
        selectorService.addInstance(DataPlaneInstance.Builder.newInstance()
                .id("dataplane")
                .url(String.format("http://localhost:%d/control/v1/dataflows", getFreePort()))
                .allowedTransferType("HttpData-PUSH")
                .allowedSourceType("HttpData")
                .build());

        var selectorClient = controlPlane.getService(DataPlaneSelectorService.class);
        ServiceResult<DataPlaneInstance> instance =
                selectorClient.select(DataAddress.Builder.newInstance().type("HttpData").build(),
                        "HttpData-PUSH", "random");

        assertThat(instance.succeeded()).isTrue();
        assertThat(instance.getContent().getId()).isEqualTo("dataplane");
    }
}
