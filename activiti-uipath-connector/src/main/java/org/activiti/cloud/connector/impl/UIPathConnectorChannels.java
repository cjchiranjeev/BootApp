/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.connector.impl;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface UIPathConnectorChannels {

    String UIPATH_CONNECTOR_CONSUMER1 = "uipathConnectorConsumer1";
    String UIPATH_CONNECTOR_CONSUMER2 = "uipathConnectorConsumer2";
    
    @Input(UIPATH_CONNECTOR_CONSUMER1)
    SubscribableChannel uipathConnectorConsumer1();
    
    @Input(UIPATH_CONNECTOR_CONSUMER2)
    SubscribableChannel uipathConnectorConsumer2();

}
