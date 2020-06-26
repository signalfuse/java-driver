/*
 * Copyright DataStax, Inc.
 *
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
package com.datastax.driver.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEndPointFactory implements EndPointFactory {

  private static final Logger logger = LoggerFactory.getLogger(ControlConnection.class);
  private static final InetAddress BIND_ALL_ADDRESS;

  static {
    try {
      BIND_ALL_ADDRESS = InetAddress.getByAddress(new byte[4]);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  private volatile Cluster cluster;

  @Override
  public void init(Cluster cluster) {
    this.cluster = cluster;
  }

  @Override
  public EndPoint create(Row peersRow) {
    if (peersRow.getColumnDefinitions().contains("native_address")) {
      InetAddress nativeAddress = peersRow.getInet("native_address");
      int nativePort = peersRow.getInt("native_port");
      InetSocketAddress translateAddress =
          cluster.manager.translateAddress(new InetSocketAddress(nativeAddress, nativePort));
      return new TranslatedAddressEndPoint(translateAddress);
    } else {
      InetAddress broadcastAddress = peersRow.getInet("peer");
      if (broadcastAddress == null) {
        return null;
      }
      InetSocketAddress translateAddress = cluster.manager.translateAddress(broadcastAddress);
      return new TranslatedAddressEndPoint(translateAddress);
    }
  }
}
