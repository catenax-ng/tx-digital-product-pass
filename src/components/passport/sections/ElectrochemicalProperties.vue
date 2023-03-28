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

<template>
  <div class="section">
    <div class="sub-section-container">
      <template v-for="(item, key) in propsData" :key="key">
        <template v-if="item && item != null && item !== ''">
          <Field
            :label="attributes[key].label"
            :data-cy="Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy') ? attributes[key]['data-cy'] : ''"
          >
          {{item}}{{Object.prototype.hasOwnProperty.call(attributes[key], 'unit') ? attributes[key]['unit'] : ''}}
          </Field>
        </template>
      </template>
    </div>
  </div>
</template>

<script>
import Field from "../generic/Field.vue";
import passportUtil from "@/utils/passportUtil.js";
import jsonUtil from "@/utils/jsonUtil.js";

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
      parent: {},
      toggle: false,
      propsData: this.$props.data.data.passport.electrochemicalProperties,
      attributes: passportUtil.getAttribute("electrochemicalProperties"),
    };
  },
  created() {
    this.propsData = passportUtil.filterAttribute(this.propsData);
    this.attributes = jsonUtil.flatternJson(this.attributes);
  },
};
</script>
