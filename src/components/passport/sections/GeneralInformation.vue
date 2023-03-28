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
    <template v-for="parent in displayKeys" :key="parent">
      <div class="sub-section-container">
        <template
          v-if="
            parent === 'batteryIdentification' &&
            Object.prototype.hasOwnProperty.call(
              propsData,
              'batteryIdentification'
            ) &&
            Object.keys(propsData['batteryIdentification']).length > 0
          "
        >
          <template v-for="(item, key) in propsData[parent]" :key="key">
            <Field
              :label="attributes[key].label"
              :data-cy="
                Object.prototype.hasOwnProperty.call(attributes[key], 'data-cy')
                  ? attributes[key]['data-cy']
                  : ''
              "
              :class="
                Object.prototype.hasOwnProperty.call(attributes[key], 'class')
                  ? attributes[key]['class']
                  : ''
              "
            >
              {{ item }}
            </Field>
          </template>
        </template>
        <template
          v-else-if="
            parent === 'manufacturer' &&
            Object.prototype.hasOwnProperty.call(propsData, 'manufacturer') &&
            Object.keys(propsData['manufacturer']).length > 0
          "
        >
          <template v-for="(item, key) in propsData[parent]" :key="key">
            <template v-if="key === 'address'">
              <AddressField
                :class="
                  Object.prototype.hasOwnProperty.call(attributes[key], 'class')
                    ? attributes[key]['class']
                    : ''
                "
                :label="attributes[key].label"
                :companyName="
                  Object.prototype.hasOwnProperty.call(
                    propsData[parent],
                    'name'
                  )
                    ? propsData[parent]['name']
                    : ''
                "
                :address="item"
              />
            </template>
            <template v-else-if="key === 'contact'">
              <ContactField
                :attributes="attributes"
                :contact="item"
                :class="
                  Object.prototype.hasOwnProperty.call(attributes[key], 'class')
                    ? attributes[key]['class']
                    : ''
                "
              />
            </template>
            <template v-else>
              <Field
                :label="attributes[key].label"
                :data-cy="
                  Object.prototype.hasOwnProperty.call(
                    attributes[key],
                    'data-cy'
                  )
                    ? attributes[key]['data-cy']
                    : ''
                "
                :class="
                  Object.prototype.hasOwnProperty.call(attributes[key], 'class')
                    ? attributes[key]['class']
                    : ''
                "
              >
                {{ item }}
              </Field>
            </template>
          </template>
        </template>
        <template
          v-else-if="
            parent === 'physicalDimensions' &&
            Object.prototype.hasOwnProperty.call(
              propsData,
              'physicalDimensions'
            ) &&
            Object.keys(propsData['physicalDimensions']).length > 0
          "
        >
          <PhysicalDimensions
            :attributes="attributes"
            :data="propsData['physicalDimensions']"
          />
          <template
            v-if="
              Object.prototype.hasOwnProperty.call(
                propsData,
                'manufacturing'
              ) && Object.keys(propsData['manufacturing']).length > 0
            "
          >
            <template
              v-if="
                Object.prototype.hasOwnProperty.call(
                  propsData['manufacturing'],
                  'dateOfManufacturing'
                )
              "
            >
              <Field
                :label="attributes['dateOfManufacturing'].label"
                :data-cy="
                  Object.prototype.hasOwnProperty.call(
                    attributes['dateOfManufacturing'],
                    'data-cy'
                  )
                    ? attributes['dateOfManufacturing']['data-cy']
                    : ''
                "
                :class="
                  Object.prototype.hasOwnProperty.call(
                    attributes['dateOfManufacturing'],
                    'class'
                  )
                    ? attributes['dateOfManufacturing']['class']
                    : ''
                "
              >
                {{ propsData["manufacturing"]["dateOfManufacturing"] }}
              </Field>
            </template>
            <template
              v-if="
                Object.prototype.hasOwnProperty.call(
                  propsData['manufacturing'],
                  'address'
                ) &&
                Object.prototype.hasOwnProperty.call(
                  propsData['manufacturing']['address'],
                  'locality'
                ) &&
                Object.prototype.hasOwnProperty.call(
                  propsData['manufacturing']['address']['locality'],
                  'value'
                )
              "
            >
              <Field
                :label="attributes['manufactureAddress'].label"
                :data-cy="
                  Object.prototype.hasOwnProperty.call(
                    attributes['manufactureAddress'],
                    'data-cy'
                  )
                    ? attributes['manufactureAddress']['data-cy']
                    : ''
                "
                :class="
                  Object.prototype.hasOwnProperty.call(
                    attributes['manufactureAddress'],
                    'class'
                  )
                    ? attributes['manufactureAddress']['class']
                    : ''
                "
              >
                {{ propsData["manufacturing"]["address"]["locality"].value }}
              </Field>
            </template>
          </template>
          <template v-for="extraField in extraFields" :key="extraField">
            <template
              v-if="Object.prototype.hasOwnProperty.call(propsData, extraField) && propsData[extraField]"
            >
              <Field
                :class="
                  Object.prototype.hasOwnProperty.call(
                    attributes[extraField],
                    'class'
                  )
                    ? attributes[extraField]['class']
                    : ''
                "
                :label="attributes[extraField].label"
              >
                {{ propsData[extraField] }}
                <template
                  v-if="
                    Object.prototype.hasOwnProperty.call(
                      attributes[extraField],
                      'unit'
                    )
                  "
                >
                  {{ attributes[extraField].unit }}
                </template>
              </Field>
            </template>
          </template>
          <template
            v-if="
              Object.prototype.hasOwnProperty.call(
                propsData,
                'stateOfBattery'
              ) && propsData['stateOfBattery'] &&
              Object.prototype.hasOwnProperty.call(
                propsData['stateOfBattery'],
                'statusBattery'
              )
            "
          >
            <Field
              :class="
                Object.prototype.hasOwnProperty.call(
                  attributes['statusBattery'],
                  'class'
                )
                  ? attributes['statusBattery']['class']
                  : ''
              "
              :label="attributes['statusBattery'].label"
            >
              {{ propsData["stateOfBattery"]["statusBattery"] }}
              <template
                v-if="
                  Object.prototype.hasOwnProperty.call(
                    attributes['statusBattery'],
                    'unit'
                  )
                "
              >
                {{ attributes["statusBattery"].unit }}
              </template>
            </Field>
          </template>
        </template>
      </div>
    </template>
  </div>
</template>

<script>
import Field from "../generic/Field.vue";
import AddressField from "../Fields/AddressField.vue";
import ContactField from "../Fields/ContactField.vue";
import PhysicalDimensions from "../Fields/PhysicalDimensions.vue";
import passportUtil from "@/utils/passportUtil.js";
import jsonUtil from "@/utils/jsonUtil.js";

export default {
  name: "GeneralInformation",
  components: {
    Field,
    AddressField,
    ContactField,
    PhysicalDimensions,
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
      extraFields: [
        "datePlacedOnMarket",
        "warrantyPeriod",
        "cO2FootprintTotal",
      ],
      attributes: passportUtil.getAllAttributes(),
      toggle: false,
      propsData: this.$props.data.data.passport,
    };
  },
  created() {
    this.attributes = jsonUtil.flatternJson(this.attributes);
    console.log(this.attributes);
    console.log(this.propsData);
  },
};
</script>
