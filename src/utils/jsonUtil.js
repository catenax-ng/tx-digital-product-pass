export default {
    exists(key, json) {
        try {
            if (Object.prototype.hasOwnProperty.call(json, key)) {
                return true;
            }
        } catch {
            //Skip
        }
        return false;
    },
    copy(json) {
        let returnValue = null
        let err = false
        try {
            Object.assign(json, returnValue) // Copy object with JS
        } catch {
            err = true;
        }
        if (err || returnValue == null) {
            try {
                returnValue = JSON.parse(JSON.stringify(json)) // If can not copy with JS copy with JSON
            } catch {
                throw new Error('Failed to copy Json [' + returnValue.toString() + ']');
            }
        }
        return returnValue
    },
    get(ref, json, sep = '.', defaultReturn = null) {
        try {
            let tmpValue = json;
            let i = 0; 
            let refs = ref.split(sep)
            while (i < refs.length) {
                const part = refs[i];
                if (!Object.prototype.hasOwnProperty.call(tmpValue, part)) {
                    return defaultReturn;
                }
                tmpValue = tmpValue[part]
                i++;
            }
            return tmpValue;
        } catch {
            return defaultReturn;
        }
    }
}
