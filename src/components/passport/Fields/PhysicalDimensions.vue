<!-- eslint-disable vue/no-multiple-template-root -->
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
  <Field :label="attributes['physicalDimensions'].label" v-if="data">
    <template v-if="data.diameter">
      {{attributes["diameter"].label}}: {{ data.diameter }}{{ attributes["diameter"].unit }},
    </template>
    <template v-if="data.length">
      {{attributes["length"].label}}: {{ data.length }}{{ attributes["length"].unit }},
    </template>
    <template v-if="data.height">
      {{attributes["height"].label}}: {{ data.height }}{{ attributes["height"].unit }},
    </template>
    <template v-if="data.width">
      {{attributes["width"].label}}: {{ data.width }}{{ attributes["width"].unit }}
    </template>
  </Field>

  <Field :label="attributes['weight'].label">
    <template v-if="data.width">
      {{ data.weight }}{{ attributes["weight"].unit }}
    </template>
  </Field>
</template>

<script>
import Field from "../generic/Field.vue";
export default {
  name: "PhysicalDimensionsComponent",
  components:{
    Field
  },
  props: {
    attributes: {type: Object, default: Object},
    data: { type: Object, default: Object },
  },
};
</script>

<style>
</style>