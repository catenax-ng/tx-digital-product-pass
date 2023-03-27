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
          <AddressField :address="seconditem.address" />
          <ContactField :contact="seconditem.contact" />
      </template>
    </template>
  </div>
</template>
<script>
import AddressField from "./AddressField.vue";
import ContactField from "./ContactField.vue";
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
    AddressField,
    ContactField,
  },
};
</script>
