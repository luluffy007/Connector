/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.api.datamanagement.transferprocess.model;

import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;

/**
 * Wrapper for a {@link TransferProcess#getId()}. Used to format a simple string as JSON.
 */
public class TransferId {
    private final String id;

    public TransferId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
