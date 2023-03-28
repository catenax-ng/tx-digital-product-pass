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
  <SubSection>
    <template #title>
      {{ label }}
    </template>
    <template #default>
      <div class="list-container">
        <ul>
          <span class="list-label"></span>
          <li v-for="attribute in attributesList" :key="attribute">
            <template v-for="(att, key, index) in attribute" :key="key">
              <span>
                <template v-if="index > 0"> - </template>{{ att }}
                <template
                  v-if="
                    Object.prototype.hasOwnProperty.call(attributes, key) &&
                    Object.prototype.hasOwnProperty.call(
                      attributes[key],
                      'unit'
                    )
                  "
                  >{{ attributes[key].unit }}</template
                >
              </span>
            </template>
          </li>
        </ul>
      </div>
    </template>
  </SubSection>
</template>

<script>
import SubSection from "@/components/passport/generic/SubSection.vue";

export default {
  components: {
    SubSection,
  },
  name: "AttributeField",
  props: {
    attributesList: { type: Array, default: () => [] },
    label: { type: [String, Number], default: "" },
    attributes: { type: Object, default: Object },
  },
};
</script>

<style scoped>
ul {
  display: flex;
  flex-direction: column;
  padding: 0;
}

li {
  margin-left: 20px;
  font-weight: bold;
}

.sub-section-container {
  display: flex;
  flex-wrap: wrap;
  border-bottom: solid 1px #edefe5;
}

.sub-title {
  font-weight: bold;
  font-size: 20px;
  color: #c6cca3;
}

.sub-title-container {
  padding: 22px 40px 0 40px;
  width: 100%;
}

.list-container {
  width: 33%;
  padding: 0 0 22px 40px;
}

.list-label {
  padding: 22px 0 10px 0;
  font-size: 12px;
  color: #777777;
}

@media (max-width: 750px) {
  .list-container {
    width: 100%;
    padding-left: 50px;
  }

  .sub-title-container {
    padding: 22px 40px 0 30px;
  }
}
</style>
