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
      <template v-for="parent in displayKeys"> 
        <template v-for="(item, key) in propsData[parent]" :key="key">
          <Field
            :label="attributes[key].label"
            :value="item"
            :data-cy="
              Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
                ? attributes[key]['data-cy']
                : ''"
          />
        </template>
      </template>
    </div>
  </div>
</template>

<script>
import Field from "../Field.vue";
import passportUtil from "@/utils/passportUtil.js";
import jsonUtil from "@/utils/jsonUtil.js";

export default {
  name: "StateOfBattery",
  components: {
    Field,
  },
  props: {
    sectionTitle: {
      type: String,
      required: false,
      default: "",
    },
    data: {
      type: Object,
      default: Object,
    },
  },

  data() {
    return {
      displayKeys: [ "stateOfBattery", "batteryCycleLife", "temperatureRangeIdleState" ],
      attributes: passportUtil.getAllAttributes(),
      toggle: false,
      propsData: this.$props.data.data.passport,
    };
  },
  created() {
    this.attributes = jsonUtil.flatternJson(this.attributes);
  },
};
</script>
