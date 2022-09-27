/*
 * Tencent is pleased to support the open source community by making Polaris available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tencent.polaris.plugin.location.remoteservice;

import com.google.protobuf.StringValue;
import com.tencent.polaris.api.utils.StringUtils;
import com.tencent.polaris.client.pb.LocationGRPCGrpc;
import com.tencent.polaris.client.pb.LocationGRPCService;
import com.tencent.polaris.client.pb.ModelProto;
import com.tencent.polaris.plugin.location.base.BaseLocationProvider;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class RemoteServiceLocationProvider extends BaseLocationProvider<RemoteServiceLocationProvider.ServiceOption> {

	private LocationGRPCGrpc.LocationGRPCBlockingStub stub;

	@Override
	public ProviderType getProviderType() {
		return ProviderType.REMOTE_SERVICE;
	}

	@Override
	public ModelProto.Location doGet(ServiceOption option) {
		buildGrpcStub(option);

		LocationGRPCService.LocationResponse response = stub.getLocation(LocationGRPCService.LocationRequest.newBuilder().build());

		return ModelProto.Location.newBuilder()
				.setRegion(StringValue.newBuilder().setValue(StringUtils.defaultString(response.getRegion())).build())
				.setZone(StringValue.newBuilder().setValue(StringUtils.defaultString(response.getZone())).build())
				.setCampus(StringValue.newBuilder().setValue(StringUtils.defaultString(response.getCampus())).build())
				.build();
	}

	public synchronized void buildGrpcStub(ServiceOption option) {
		if (stub != null) {
			return;
		}

		ManagedChannel channel = ManagedChannelBuilder.forTarget(option.getTarget()).build();
		stub = LocationGRPCGrpc.newBlockingStub(channel);
	}

	public static class ServiceOption {

		private String target;

		String getTarget() {
			return target;
		}

		void setTarget(String target) {
			this.target = target;
		}
	}
}
