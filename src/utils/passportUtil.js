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

import jsonUtil from "@/utils/jsonUtil.js";
import attributes from "@/config/attributes.json";

export default {
    getAttribute(attribute, sep = ".", defaultValue = null) {
        return jsonUtil.get(attribute, attributes, sep, defaultValue);
    },
    filterAttribute(attributes) {
        if (!attributes) return null;
        if (!(attributes instanceof Object)) return attributes;
        let tmpAttributes = jsonUtil.copy(attributes);
        let tmpPropsData = {};
        for (let attr in tmpAttributes) {
            let tmpData = tmpAttributes[attr];

            if (tmpData == null) continue;
            tmpData = jsonUtil.flatternJson(tmpData);

            tmpPropsData = jsonUtil.extend(tmpPropsData, tmpData);
        }
        if (Object.keys(tmpPropsData).length < 0) return null;
        return tmpPropsData;
    },
};
