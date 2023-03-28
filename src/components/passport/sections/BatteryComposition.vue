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

<template v-if="propsData">
  <div class="section">
    <!-- Composition of battery -->
    <template v-for="(item, key) in propsData" :key="key">
      <AttributeField
        v-if="(item instanceof Array)"
        :attributes-list="item"
        :label="attributes[key].label"
        :data-cy="
          Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
            ? attributes[key]['data-cy']
            : ''
        "
      />
      <SubSection
        v-else
        :data-cy="
          Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
            ? attributes[key]['data-cy']
            : ''
        "
      >
        <template #title>
          {{ attributes[key].label }}
        </template>
        <template #default>
          <template v-if="(item instanceof Object) && key === 'components'">
             <ComponentField :data="item" :attributes="attributes"/> 
          </template>
          <template v-else>
            <div class="list-container">
              <ul>
                <span class="list-label"></span>
                <li>
                  <span>
                    {{ item }}
                  </span>
                </li>
              </ul>
            </div>
          </template>
        </template>
      </SubSection>
    </template>
  </div>
</template>

<script>
import ComponentField from "../Fields/ComponentField.vue";
import AttributeField from "../Fields/AttributeField.vue";
import passportUtil from "@/utils/passportUtil.js";
import jsonUtil from "@/utils/jsonUtil.js";
import SubSection from "../generic/SubSection.vue";
export default {
  name: "BatteryComposition",
  components: {
    AttributeField,
    ComponentField,
    SubSection
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
      attributes: passportUtil.getAttribute("composition"),
      toggle: false,
      propsData: this.$props.data.data.passport.composition,
    };
  },
  created() {
    this.attributes = jsonUtil.flatternJson(this.attributes);
  },
};
</script>
