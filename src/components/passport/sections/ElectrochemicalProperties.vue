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
      <Field
        data-cy="remaining-capacity"
        :label="attributes['ratedCapacity'].label"
        :unit="attributes['ratedCapacity'].unit"
        :value="propsData.ratedCapacity"
      />
      <Field
        :label="batteryEnergy['energyRoundtripEfficiencyChange'].label"
        :unit="batteryEnergy['energyRoundtripEfficiencyChange'].unit"
        :value="propsData.batteryEnergy.energyRoundtripEfficiencyChange"
      />
      <Field
        :label="batteryEnergy['maximumAllowedBatteryEnergy'].label"
        :unit="batteryEnergy['maximumAllowedBatteryEnergy'].unit"
        :value="propsData.batteryEnergy.maximumAllowedBatteryEnergy"
      />
      <Field
        :label="batteryEnergy['energyRoundtripEfficiency'].label"
        :unit="batteryEnergy['energyRoundtripEfficiency'].unit"
        :value="propsData.batteryEnergy.energyRoundtripEfficiency"
      />

      <Field
        :label="ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy['title'].label"
        :unit="ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy['title'].unit"
        :value="
          propsData.ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy
        "
      />
      <Field
        :label="batteryVoltage['nominalVoltage'].label"
        :unit="batteryVoltage['nominalVoltage'].unit"
        :value="propsData.batteryVoltage.nominalVoltage"
      />
      <Field
        :label="batteryVoltage['maxVoltage'].label"
        :unit="batteryVoltage['maxVoltage'].unit"
        :value="propsData.batteryVoltage.maxVoltage"
      />
      <Field
        :label="batteryVoltage['minVoltage'].label"
        :unit="batteryVoltage['minVoltage'].unit"
        :value="propsData.batteryVoltage.minVoltage"
      />
      <Field
        :label="internalResistance['cellInternalResistance'].label"
        :unit="internalResistance['cellInternalResistance'].label"
        :value="propsData.internalResistance.cellInternalResistance"
      />
      <Field
        :label="internalResistance['packInternalResistanceIncrease'].label"
        :unit="internalResistance['packInternalResistanceIncrease'].label"
        :value="propsData.internalResistance.packInternalResistanceIncrease"
      />
      <Field
        :label="internalResistance['packInternalResistance'].label"
        :unit="internalResistance['packInternalResistance'].label"
        :value="propsData.internalResistance.packInternalResistance"
      />

      <Field
        :label="capacityThresholdExhaustion['title'].label"
        :unit="capacityThresholdExhaustion['title'].label"
        :value="propsData.capacityThresholdExhaustion"
      />
      <Field
        :label="batteryPower['originalPowerCapability'].label"
        :unit="batteryPower['originalPowerCapability'].unit"
        :value="propsData.batteryPower.originalPowerCapability"
      />
      <Field
        :label="batteryPower['powerFade'].label"
        :unit="batteryPower['powerFade'].unit"
        :value="propsData.batteryPower.powerFade"
      />
      <Field
        :label="batteryPower['originalPowerCapabilityLimits'].label"
        :unit="batteryPower['originalPowerCapabilityLimits'].unit"
        :value="propsData.batteryPower.originalPowerCapabilityLimits"
      />
      <Field
        :label="batteryPower['maximumAllowedBatteryPower'].label"
        :unit="batteryPower['maximumAllowedBatteryPower'].unit"
        :value="propsData.batteryPower.maximumAllowedBatteryPower"
      />
      <Field
        :label="batteryPower['powerCapabilityAt20Charge'].label"
        :unit="batteryPower['powerCapabilityAt20Charge'].unit"
        :value="propsData.batteryPower.powerCapabilityAt20Charge"
      />
      <Field
        :label="batteryPower['powerCapabilityAt80Charge'].label"
        :unit="batteryPower['powerCapabilityAt80Charge'].unit"
        :value="propsData.batteryPower.powerCapabilityAt80Charge"
      />
      <Field 
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
      batteryEnergy: passportUtil.getAttribute("electrochemicalProperties.batteryEnergy"),
      batteryVoltage: passportUtil.getAttribute("electrochemicalProperties.batteryVoltage"),
      internalResistance: passportUtil.getAttribute("electrochemicalProperties.internalResistance"),
      ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy: passportUtil.getAttribute("electrochemicalProperties.ratioMaximumAllowedBatteryPowerAndMaximumAllowedBatteryEnergy"),
      capacityThresholdExhaustion: passportUtil.getAttribute("electrochemicalProperties.capacityThresholdExhaustion"),
      batteryPower: passportUtil.getAttribute("electrochemicalProperties.batteryPower"),
      capacityFade: passportUtil.getAttribute("electrochemicalProperties.capacityFade"),

      toggle: false,
      propsData: this.$props.data.data.passport.electrochemicalProperties,
    };
  },
};
</script>
