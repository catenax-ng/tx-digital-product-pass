<!-- eslint-disable vue/no-multiple-template-root -->
<!-- eslint-disable vue/no-v-for-template-key -->
<template>
  <div v-for="(parent, parentkey) in data" :key="parentkey" class="list-container">
    <template v-if="parentkey === 'componentsPartNumber'">
      <template v-for="(item, key) in parent" :key="key">
          <ul>
            <span class="list-label">{{
              attributes["componentsPartNumber"].label
            }}</span>
            <li>
              <span>
                {{ item }}
              </span>
            </li>
          </ul>
      </template>
    </template>
    <template v-else-if="parentkey === 'componentsSupplier'">
      <template
        v-for="(seconditem, secondkey) in parent"
        :key="secondkey"
      >
          <AddressAttributeField :address="seconditem.address" />
          <ContactAttributeField :contact="seconditem.contact" />
      </template>
    </template>
  </div>
</template>
<script>
import AddressAttributeField from "./AddressAttributeField.vue";
import ContactAttributeField from "./ContactAttributeField.vue";
export default {
  name: "ComponentField",
  props: {
    data: {
      type: Object,
      default: Object,
    },
    attributes: {
      type: Object,
      default: Object,
    },
  },
  components: {
    AddressAttributeField,
    ContactAttributeField,
  },
};
</script>
