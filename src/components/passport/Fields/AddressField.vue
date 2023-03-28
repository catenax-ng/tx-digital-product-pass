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
            <span v-if="city" class="field-value">{{ city ? city : "—" }} </span>
            <span v-if="postal" class="field-value">{{ postal ? postal : "—" }} </span>
            <span v-if="country" class="field-value"
            >{{ country ? country : "—" }}
        </span>
    </div>
</template>

<script>
export default {
  name: "AddressField",
  props: {
    city: { type: [String, Number], default: "" },
    postal: { type: [String, Number], default: "" },
    country: { type: [String, Number], default: "" },
  }
}
</script>

<style>

</style>