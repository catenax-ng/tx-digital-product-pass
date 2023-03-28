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

      <template v-for="parent in displayKeys" :key="parent"> 
        <template v-for="(item, key) in propsData[parent]" :key="key">
          <Field
            :label="attributes[key].label"
            :data-cy="
              Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
                ? attributes[key]['data-cy']
                : ''"
            :class="Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
                ? attributes[key]['class']
                : ''"
          >
          {{item}}
          </Field>
        </template>
      </template>
    </div>
    <!-- <div v-if="propsData.manufacturer" class="sub-section-container">
      <Field
        class="full-width"
        :label="manufacturer['manufacturerInformation'].label"
        :value="propsData.manufacturer.name"
      />
      <Field
        class="longer"
        :label="manufacturer['address'].label"
        :city="propsData.manufacturer.address.locality.value"
        :country="propsData.manufacturer.address.country.shortName"
        :postal="propsData.manufacturer.address.postCode.value"
        :value="propsData.manufacturer.name"
      />
      <Field
        :label="manufacturer['phoneNumber'].label"
        :value="propsData.manufacturer.contact.phoneNumber"
      />
      <Field
        :label="manufacturer['email'].label"
        :value="propsData.manufacturer.contact.email"
      />
    </div>
    <div v-if="propsData.physicalDimensions" class="sub-section-container">
      <Field
        :label="physicalDimensions.label"
        :height="propsData.physicalDimensions.height"
        :length="propsData.physicalDimensions.length"
        :unit="physicalDimensions.unit"
        :width="propsData.physicalDimensions.width"
      />

      <Field
        :label="physicalDimensions['weight'].label"
        :unit="physicalDimensions['weight'].unit"
        :value="propsData.physicalDimensions.weight"
      />

      <Field
        :label="manufacturing['dateOfManufacturing'].label"
        :day="propsData.manufacturing.dateOfManufacturing"
      />
      <Field
        :label="manufacturing['placeOfManufacturing'].label"
        :value="propsData.manufacturing.address.locality.value"
      />
      <Field
        class="two-third-width"
        :label="datePlacedOnMarket.label"
        :day="propsData.datePlacedOnMarket"
      />
      <Field
        class="longer"
        :label="warrantyPeriod.label"
        :value="propsData.warrantyPeriod"
      />
      <Field
        :label="stateOfBattery['statusBattery'].label"
        :value="
          propsData.stateOfBattery
            ? propsData.stateOfBattery.statusBattery
            : null
        "
      />
      <Field
        :label="cO2FootprintTotal['cO2FootprintTotal'].label"
        :unit="cO2FootprintTotal['cO2FootprintTotal'].unit"
        :value="propsData.cO2FootprintTotal"
      />
    </div> -->
  </div>
</template>

<script>
import Field from "../generic/Field.vue";
import passportUtil from "@/utils/passportUtil.js";
import jsonUtil from "@/utils/jsonUtil.js";

export default {
  name: "GeneralInformation",
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
      displayKeys: [
        "batteryIdentification",
        "manufacturer",
        "physicalDimensions",
      ],
      attributes: passportUtil.getAllAttributes(),
      toggle: false,
      propsData: this.$props.data.data.passport,
    };
  },
  created(){
   this.attributes = jsonUtil.flatternJson(this.attributes);
  }
};
</script>
