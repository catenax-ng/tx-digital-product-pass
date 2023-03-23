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
      <Field
        v-if="propsData.ratedCapacity != null"
        data-cy="remaining-capacity"
        :label="attributes1['ratedCapacity'].label"
        :unit="attributes1['ratedCapacity'].unit"
        :value="propsData.ratedCapacity"
      />
      <Field
        v-if="propsData.batteryEnergy != null && propsData.batteryEnergy.energyRoundtripEfficiencyChange != null"
        :label="batteryEnergy['energyRoundtripEfficiencyChange'].label"
        :unit="batteryEnergy['energyRoundtripEfficiencyChange'].unit"
        :value="propsData.batteryEnergy.energyRoundtripEfficiencyChange"
      />
      <Field
        v-if="propsData.batteryEnergy != null && propsData.batteryEnergy.maximumAllowedBatteryEnergy != null"
        :label="batteryEnergy['maximumAllowedBatteryEnergy'].label"
        :unit="batteryEnergy['maximumAllowedBatteryEnergy'].unit"
        :value="propsData.batteryEnergy.maximumAllowedBatteryEnergy"
      />
      <Field
        v-if="propsData.batteryEnergy != null && propsData.batteryEnergy.energyRoundtripEfficiency != null"
        :label="batteryEnergy['energyRoundtripEfficiency'].label"
        :unit="batteryEnergy['energyRoundtripEfficiency'].unit"
        :value="propsData.batteryEnergy.energyRoundtripEfficiency"
      />

      <Field
        v-if="propsData.ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy != null"
        :label="ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy['title'].label"
        :unit="ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy['title'].unit"
        :value="propsData.ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy"
      />
      <Field
        v-if="propsData.batteryVoltage != null && propsData.batteryVoltage.nominalVoltage != null"
        :label="batteryVoltage['nominalVoltage'].label"
        :unit="batteryVoltage['nominalVoltage'].unit"
        :value="propsData.batteryVoltage.nominalVoltage"
      />
      <Field
        v-if="propsData.batteryVoltage != null && propsData.batteryVoltage.maxVoltage != null"
        :label="batteryVoltage['maxVoltage'].label"
        :unit="batteryVoltage['maxVoltage'].unit"
        :value="propsData.batteryVoltage.maxVoltage"
      />
      <Field
        v-if="propsData.batteryVoltage != null && propsData.batteryVoltage.minVoltage != null"
        :label="batteryVoltage['minVoltage'].label"
        :unit="batteryVoltage['minVoltage'].unit"
        :value="propsData.batteryVoltage.minVoltage"
      />
      <Field
        v-if="propsData.internalResistance != null && propsData.internalResistance.cellInternalResistance != null"
        :label="internalResistance['cellInternalResistance'].label"
        :unit="internalResistance['cellInternalResistance'].label"
        :value="propsData.internalResistance.cellInternalResistance"
      />
      <Field
        v-if="
          propsData.internalResistance != null && propsData.internalResistance.packInternalResistanceIncrease != null
        "
        :label="internalResistance['packInternalResistanceIncrease'].label"
        :unit="internalResistance['packInternalResistanceIncrease'].label"
        :value="propsData.internalResistance.packInternalResistanceIncrease"
      />
      <Field
        v-if="propsData.internalResistance != null && propsData.internalResistance.packInternalResistance != null"
        :label="internalResistance['packInternalResistance'].label"
        :unit="internalResistance['packInternalResistance'].label"
        :value="propsData.internalResistance.packInternalResistance"
      />

      <Field
        v-if="propsData.capacityThresholdExhaustion != null"
        :label="capacityThresholdExhaustion['title'].label"
        :unit="capacityThresholdExhaustion['title'].label"
        :value="propsData.capacityThresholdExhaustion"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.originalPowerCapability != null"
        :label="batteryPower['originalPowerCapability'].label"
        :unit="batteryPower['originalPowerCapability'].unit"
        :value="propsData.batteryPower.originalPowerCapability"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.powerFade != null"
        :label="batteryPower['powerFade'].label"
        :unit="batteryPower['powerFade'].unit"
        :value="propsData.batteryPower.powerFade"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.originalPowerCapabilityLimits != null"
        :label="batteryPower['originalPowerCapabilityLimits'].label"
        :unit="batteryPower['originalPowerCapabilityLimits'].unit"
        :value="propsData.batteryPower.originalPowerCapabilityLimits"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.maximumAllowedBatteryPower != null"
        :label="batteryPower['maximumAllowedBatteryPower'].label"
        :unit="batteryPower['maximumAllowedBatteryPower'].unit"
        :value="propsData.batteryPower.maximumAllowedBatteryPower"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.powerCapabilityAt20Charge != null"
        :label="batteryPower['powerCapabilityAt20Charge'].label"
        :unit="batteryPower['powerCapabilityAt20Charge'].unit"
        :value="propsData.batteryPower.powerCapabilityAt20Charge"
      />
      <Field
        v-if="propsData.batteryPower != null && propsData.batteryPower.powerCapabilityAt80Charge != null"
        :label="batteryPower['powerCapabilityAt80Charge'].label"
        :unit="batteryPower['powerCapabilityAt80Charge'].unit"
        :value="propsData.batteryPower.powerCapabilityAt80Charge"
      />
      <Field
        v-if="propsData.capacityFade != null"
        :label="capacityFade['title'].label"
        :unit="capacityFade['title'].unit"
        :value="propsData.capacityFade"
      />
    </div>
  </div>
</template>

<script>
import Field from "../Field.vue";
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
      attributes: {},
      toggle: false,
      propsData: this.$props.data.data.passport.electrochemicalProperties,
      attributes1: passportUtil.getAttribute("electrochemicalProperties"),
      batteryEnergy: passportUtil.getAttribute("electrochemicalProperties.batteryEnergy"),
      batteryVoltage: passportUtil.getAttribute("electrochemicalProperties.batteryVoltage"),
      internalResistance: passportUtil.getAttribute("electrochemicalProperties.internalResistance"),
      ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy: passportUtil.getAttribute(
        "electrochemicalProperties.ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy"
      ),
      capacityThresholdExhaustion: passportUtil.getAttribute("electrochemicalProperties.capacityThresholdExhaustion"),
      batteryPower: passportUtil.getAttribute("electrochemicalProperties.batteryPower"),
      capacityFade: passportUtil.getAttribute("electrochemicalProperties.capacityFade"),
    };
  },
  created() {
    this.parent = passportUtil.getAttribue("electrochemicalProperties");
    this.attributes = this.getProperties(this.data);
    console.log(this.attributes);
  },
  methods: {
    getProperties(parentObject, properties = {}) {
      if (!(parentObject instanceof Object)) return properties;

      for (let key in parentObject) {
        let item = parentObject[key];
        if (!(item instanceof Object)) {
          properties[key] = item;
          continue;
        }

        properties[key] = jsonUtil.get(key, parentObject[key]);
      }
      return properties;
    },
  },
};
</script>
