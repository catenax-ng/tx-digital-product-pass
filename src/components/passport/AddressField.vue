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
  <List title="Address">
    <p>{{ address.locality.value }}</p>
    <p>{{ address.country.shortName }}</p>
    <p>{{ address.postCode.value }}</p>
    <p>
      {{ address.thoroughfare.value }}
      {{ address.thoroughfare.number }}
    </p>
    <p>{{ address.premise.value }}</p>
    <p>{{ address.postalDeliveryPoint.value }}</p>
  </List>
</template>
<script>
import List from "@/components/general/List.vue";
export default {
  name: "AddressField",
  components: {
    List,
  },
  props: {
    address: {
      type: Object,
      default: Object,
    },
  },
};
</script>