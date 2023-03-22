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
      <Field
        :label="stateofBattery['stateOfHealth'].label"
        :value="propsData.stateOfBattery.stateOfHealth"
      />
      <Field
        :label="stateofBattery['statusBattery'].label"
        :value="propsData.stateOfBattery.statusBattery"
      />
      <Field
        :data-cy="stateofBattery['stateOfCharge'].data-cy"
        :label="stateofBattery['stateOfCharge'].label"
        :value="propsData.stateOfBattery.stateOfCharge"
      />
      <Field
        :label="batteryCycleLife['cycleLifeTestCRate'].label"
        :value="propsData.batteryCycleLife.cycleLifeTestCRate"
      />
      <Field
        :label="batteryCycleLife['cycleLifeTestDepthOfDischarge'].label"
        :value="propsData.batteryCycleLife.cycleLifeTestDepthOfDischarge"
      />
      <Field
        :label="batteryCycleLife['expectedLifetime'].label"
        :value="propsData.batteryCycleLife.expectedLifetime"
      />
      <Field
        :label="temperatureRangeIdleState['temperatureRangeIdleStateUpperLimit'].label"
        :value="
          propsData.temperatureRangeIdleState
            .temperatureRangeIdleStateUpperLimit
        "
      />
      <Field
        :label="temperatureRangeIdleState['temperatureRangeIdleStateLowerLimit'].label"
        :value="
          propsData.temperatureRangeIdleState
            .temperatureRangeIdleStateLowerLimit
        "
      />
    </div>
  </div>
</template>

<script>
import Field from "../Field.vue";
import passportUtil from "@/utils/passportUtil.js";

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
      stateofBattery: passportUtil.getAttribute("stateOfBattery"),
      batteryCycleLife: passportUtil.getAttribute("batteryCycleLife"),
      temperatureRangeIdleState: passportUtil.getAttribute("temperatureRangeIdleState"),
      toggle: false,
      propsData: this.$props.data.data.passport,
    };
  },
};
</script>
