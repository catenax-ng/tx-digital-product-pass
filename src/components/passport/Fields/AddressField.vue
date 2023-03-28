<!--
 Copyright 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<template>
  <div class="field-container">
    <span class="field-label">{{ label }}</span>
    <span v-if="companyName" class="field-value">{{companyName}}</span>
    <span v-if="address.locality" class="field-value">{{ address.locality.value ? address.locality.value : "—" }} </span>
    <span v-if="address.postCode" class="field-value">{{ address.postCode.value ? address.postCode.value : "—" }} </span>
    <span v-if="address.country" class="field-value"
      >{{ address.country.shortName ? address.country.shortName : "—" }}
    </span>
  </div>
</template>

<script>
export default {
  name: "AddressField",
  props: {
    label: { type: String, default: "" },
    address: {type: Object, default: Object},
    companyName: {type: String, default: ""} 
  },
};
</script>

<style>
</style>