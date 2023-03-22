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
    <!-- Composition of battery -->
    <AttributeField
      :attributes-list="propsData.compositionOfBattery"
      :label="composition['compositionOfBattery'].label"
    />
    <!-- Critical raw materials -->
    <div class="sub-section-container">
      <div class="sub-title-container">
        <span class="sub-title">{{composition['criticalRawMaterials'].label}}</span>
      </div>
      <div v-if="propsData.criticalRawMaterials" class="list-container">
        <ul>
          <span class="list-label"></span>
          <li>
            <span>
              {{ propsData.criticalRawMaterials }}
            </span>
          </li>
        </ul>
      </div>
    </div>
    <!-- Components -->
    <div class="sub-section-container">
      <div class="sub-title-container">
        <span class="sub-title">{{components['title'].label}}</span>
      </div>
      <div v-if="propsData.components" class="list-container">
        <ul>
          <span class="list-label">{{components['componentsPartNumber'].label}}</span>
          <li>
            <span>
              {{ propsData.components.componentsPartNumber }}
            </span>
          </li>
        </ul>
      </div>
      <div
        v-if="propsData.components.componentsSupplier"
        class="list-container"
      >
        <ul>
          <span class="list-label">{{componentsSupplier['address'].label}}</span>
          <li
            v-for="supplierDetails in propsData.components.componentsSupplier"
            :key="supplierDetails"
          >
            <p>{{ supplierDetails.address.locality.value }}</p>
            <p>{{ supplierDetails.address.country.shortName }}</p>
            <p>{{ supplierDetails.address.postCode.value }}</p>
            <p>
              {{ supplierDetails.address.thoroughfare.value }}
              {{ supplierDetails.address.thoroughfare.number }}
            </p>
            <p>{{ supplierDetails.address.premise.value }}</p>
            <p>{{ supplierDetails.address.postalDeliveryPoint.value }}</p>
          </li>
        </ul>
        <ul>
          <span class="list-label">{{componentsSupplier['contact'].label}}</span>
          <li
            v-for="supplierDetails in propsData.components.componentsSupplier"
            :key="supplierDetails"
          >
            <p>{{componentsSupplier['faxNumber'].label}}: {{ supplierDetails.contact.faxNumber }}</p>
            <p>{{componentsSupplier['website'].label}}: {{ supplierDetails.contact.website }}</p>
            <p>{{componentsSupplier['phoneNumber'].label}}: {{ supplierDetails.contact.phoneNumber }}</p>
            <p>
              {{componentsSupplier['email'].label}}:
              {{ supplierDetails.contact.email }}
            </p>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import AttributeField from "../AttributeField.vue";
import passportUtil from "@/utils/passportUtil.js";

export default {
  name: "BatteryComposition",
  components: {
    AttributeField,
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
      composition: passportUtil.getAttribute("composition"),
      components: passportUtil.getAttribute("composition.components"),
      componentsSupplier: passportUtil.getAttribute("composition.components.componentsSupplier"),
      toggle: false,
      propsData: this.$props.data.data.passport.composition
    };
  }
  
};
</script>
