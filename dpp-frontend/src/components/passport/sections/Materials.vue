<template v-if="propsData">
  <div class="section">
    <v-container class="ma-0">
      <v-row class="section">
        <v-col sm="12" md="4" class="pa-0 ma-0">
          <template v-if="propsData.active.other">
            <template v-for="attr in propsData.active.other" :key="attr">
              <div class="element-chart-label">
                {{ $t("sections.materials.otherMaterials") }}
              </div>
              <Field
                icon="name"
                :value="attr.materialName.name"
                :label="$t('sections.materials.name')"
              />
              <Field
                icon="type"
                :value="attr.materialName.type"
                :label="$t('sections.materials.type')"
              />
              <Field
                :icon="attr.location"
                :value="attr.location"
                :label="$t('sections.materials.location')"
              />
              <Field
                icon="recycled"
                :value="attr.recycled"
                :label="$t('sections.materials.recycledContent')"
                unit="%"
              />
              <template
                v-for="attrChild in attr.materialIdentification"
                :key="attr"
              >
                <Field
                  icon="identification"
                  :value="attrChild.type + ':' + ' ' + attrChild.id"
                  :label="$t('sections.materials.materialIdentification')"
                />
              </template>
              <template v-for="attrChild in attr.documentation" :key="attr">
                <Field
                  icon="documentation"
                  :value="attrChild.content"
                  :label="attrChild.header"
                />
              </template>
            </template>
          </template>
          <template v-if="propsData.hazardous">
            <div class="element-chart-label">
              {{ $t("sections.materials.hazardous") }}
            </div>
            <template v-for="(attr, key) in propsData.hazardous" :key="key">
              <Field
                icon="name"
                :value="attr.concentration"
                unit="%"
                :label="key"
              />
            </template>
          </template>
        </v-col>
        <v-col sm="12" md="4" class="pa-0 ma-0">
          <div class="element-chart-label"></div>
          <div class="battery-graph">
            <div class="graph-icon">
              <v-icon>mdi-battery-outline</v-icon>
            </div>

            <div class="composition-graph">
              <div
                v-for="(detail, detailIndex) in formattedComposition"
                :key="detailIndex"
                class="composition-section"
                :style="
                  detailIndex === 0 ? 'align-items: end' : 'align-items: start'
                "
              >
                <div
                  class="composition-title"
                  :style="
                    detailIndex === 0
                      ? 'align-self: flex-start'
                      : 'align-self: flex-end'
                  "
                >
                  {{ detail.title }}
                </div>
                <div class="composition-bar-container">
                  <div
                    v-for="(component, index) in detail.composition"
                    :key="index"
                    class="composition-bar"
                    :style="[
                      {
                        height: component.value * 1.5 + 'px',
                        backgroundColor: getColor(detailIndex + '.' + index),
                      },
                      detailIndex === 0 && index === 0
                        ? { 'border-top-left-radius': '6px' }
                        : {},
                      detailIndex === 1 && index === 0
                        ? { 'border-top-right-radius': '6px' }
                        : {},
                      detailIndex === 1 &&
                      index === detail.composition.length - 1
                        ? { 'border-bottom-right-radius': '6px' }
                        : {},
                      detailIndex === 0 &&
                      index === detail.composition.length - 1
                        ? { 'border-bottom-left-radius': '6px' }
                        : {},
                    ]"
                  >
                    <div
                      :style="detailIndex === 0 ? 'right:12px' : 'left:12px'"
                      class="component-label-line"
                    ></div>
                    <div
                      class="component-label-container"
                      :style="{
                        left: detailIndex === 0 ? '0' : 'auto',
                        right: detailIndex === 0 ? 'auto' : '0',
                      }"
                    >
                      {{ component.label }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <p class="type">Battery composition</p>
          </div>
        </v-col>
        <v-col sm="12" md="4" class="pa-0 ma-0">
          <div class="element-chart-label" style="margin-bottom: 15px">
            {{ $t("sections.cellChemistry.recyclateContent") }}
          </div>
          <ElementChart :data="propsData.active" style="margin-left: 12px" />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import Field from "../Field.vue";
import ElementChart from "../../passport/ElementChart.vue";

export default {
  name: "CellChemistry",
  components: {
    Field,
    ElementChart,
  },
  props: {
    data: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      formattedComposition: [],
      propsData: this.$props.data.aspect?.materials || [],
      electrolyteComposition: [],
    };
  },
  methods: {
    parseData() {
      const compositionsByLocation = {};

      this.propsData.composition.forEach((item) => {
        const { location, name, concentration, unit } = item;
        if (!compositionsByLocation[location]) {
          compositionsByLocation[location] = {
            title: `Composition of ${location}`,
            composition: [],
          };
        }
        compositionsByLocation[location].composition.push({
          label: name.name,
          value: concentration,
          unit: "%",
        });
      });

      this.formattedComposition = Object.values(compositionsByLocation);
    },
    completeComponents() {
      this.formattedComposition.forEach((detail) => {
        let total = detail.composition.reduce(
          (sum, component) => sum + component.value,
          0
        );
        if (total < 100) {
          detail.composition.push({
            label: "Other",
            value: 100 - total,
            unit: "%",
          });
        } else if (total > 100) {
          let factor = 100 / total;
          detail.composition = detail.composition.map((component) => ({
            ...component,
            value: component.value * factor,
          }));
        }
      });
    },
    getColor(label) {
      const colors = {
        "0.0": "#676BC6",
        0.1: "#FFEBCC",
        0.2: "#FFD700",
        0.3: "#BDB76B",
        0.4: "#FF4500",
        0.5: "#2E8B57",
        0.6: "#D2691E",
        "1.0": "#88982D",
        1.1: "#428C5B",
        1.2: "#F0F5D5",
        1.3: "#337B89",
        1.4: "#303030",
        1.5: "#486079",
        1.6: "#008B8B",
        1.7: "#B8860B",
        1.8: "#32CD32",
        1.9: "#FFA07A",
        "1.10": "#6A5ACD",
      };
      return colors[label] || "#333";
    },
  },
  mounted() {
    this.parseData();
    this.completeComponents();
  },
};
</script>
