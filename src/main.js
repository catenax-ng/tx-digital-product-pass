/**
 * Copyright 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { createApp } from 'vue';
import App from './App.vue';
import store from './store';
import vuetify from './assets/plugins/vuetify';
import { loadFonts } from './assets/plugins/webfontloader';
import router from './router';
import '@/assets/styles/main.scss';
import authentication from '@/services/Authentication';

loadFonts();

const app = createApp(App);
app.use(vuetify);
app.use(store);
app.use(router);

let auth = new authentication();
app.provide('authentication', auth);
auth.keycloakInit(app);
