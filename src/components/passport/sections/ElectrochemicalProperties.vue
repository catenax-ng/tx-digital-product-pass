<!-- eslint-disable vue/no-v-for-template-key -->
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

<template  >
  <div class="section">
    <div class="sub-section-container">
      <template v-for="(item, key) in propsData" :key="key">
        <Field
          :value="item"
          :label="attributes[key].label"
          :unit="
            Object.prototype.hasOwnProperty.call(attributes[key], 'unit')
              ? attributes[key]['unit']
              : ''
          "
          :data-cy="
            Object.prototype.hasOwnProperty.call(attributes[key], 'dataCy')
              ? attributes[key]['dataCy']
              : ''
          "
        />
      </template>
    </div>
  </div>
</template>

<script>
import Field from "../Field.vue";
import passportUtil from "@/utils/passportUtil.js";

export default {
  name: "ElectrochemicalProperties",
  components: {
    Field,
  },
  props: {
    sectionTitle: {
      type: String,
      default: "",
    },
    data: {
      type: Object,
      default: Object,
    },
  },
  data() {
    return {
      attributes: passportUtil.getAttribute("electrochemicalProperties"),
      toggle: false,
      propsData: this.$props.data.data.passport.electrochemicalProperties,
    };
  },
};
</script>
