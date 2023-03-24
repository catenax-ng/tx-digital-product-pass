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
    },
    getUniqueId(originalKey, json) {
        let uniqueKey = (' ' + originalKey).slice(1); // Deep Copy String
        let i = 0;
        while (Object.prototype.hasOwnProperty.call(json, uniqueKey)) { // Search if the key exists in the JSON
            uniqueKey = originalKey + "_" + i; // If exists we add a number and check again
            i++;
        }
        return uniqueKey //Return unique id
    }, 
    flatternJson(json, allowNull=false, allowEmpty=false) {
        // Deep Copy param into objects
        let objects = JSON.parse(JSON.stringify(json));
        let retObjects = {}; // Return/Final Object
        let keys = Object.keys(objects); // Keys that it contains
    
        while (keys.length > 0) { // While it still has keys
            for (let index in keys) { // Interate over keys
                let parentKey = keys[index]; // Get key value in array
                let parent = objects[parentKey]; // Get current node value
                delete objects[parentKey]; // Delete current node from interation object
                
                if(parent == null && !allowNull){ // If nulls are not allowed
                    continue;
                }

                if (!(parent instanceof Object)) { // If current node is not a object
                    // Check if key is not existing
                    let cleanParentKey = this.getUniqueId(parentKey, retObjects);
                    retObjects[cleanParentKey] = parent; // Store value (string, int, array, etc...) (if is not object)
                    continue;
                }
    
                let tmpCleanParent = {}; // Clean parent without children
                for (let childKey in parent) { // Interate over children
                    let child = parent[childKey]; // Get children 
    
                    if(child == null && !allowNull){ // If nulls are not allowed
                        continue;
                    }

                    if (!(child instanceof Object)) { // If children is not a object is a property from the father
                        // Check if key is not existing
                        let childstoreKey = this.getUniqueId(childKey, tmpCleanParent);
                        tmpCleanParent[childstoreKey] = child; // Store property on father
                        continue;
                    }
                    //Child is a object
                    if (allowEmpty || Object.keys(child).length > 0) { // If the children has keys store in the objects
                        // Check if key is not existing
                        let childstoreKey = this.getUniqueId(childKey, objects);
                        objects[childstoreKey] = child; // Store in the interation objects
                    }
                }
            
                if (allowEmpty || Object.keys(tmpCleanParent).length > 0) { // If the father has content
                    let storeKey = this.getUniqueId(parentKey, retObjects);
                    retObjects[storeKey] = tmpCleanParent; // Store the father in the return objects
                }
            }
            if(objects) // If objects is not undefined continue interation
                keys = Object.keys(objects); // Look for the keys again
        }
        return retObjects; // Return clean objects
    },
    buildPath(parentKey, key, sep=".") {
        return [parentKey, key].join(sep);
    },
    mapJson(json, allowNull=false, allowEmpty=false) {
        // Deep Copy param into object
        let objects = JSON.parse(JSON.stringify(json));
        let retKeys = []; // Return/Final Object
        let keys = Object.keys(objects); // Keys that it contains
    
        while (keys.length > 0) { // While it still has keys
            for (let index in keys) { // Interate over keys
                let parentKey = keys[index]; // Get key value in array
                let parent = objects[parentKey]; // Get current node value
                delete objects[parentKey]; // Delete current node from interation object

                if(parent == null && !allowNull){ // If nulls are not allowed
                    continue;
                }

                if (!(parent instanceof Object)) { // If current node is not a object
                    let cleanParentKey = parentKey;
                    retKeys.push(cleanParentKey);
                    continue;
                }
    
                let tmpCleanParent = {}; // Clean parent without children
                for (let childKey in parent) { // Interate over children
                    let child = parent[childKey]; // Get children 
    
                    if(child == null && !allowNull){ // If nulls are not allowed
                        continue;
                    }

                    if (!(child instanceof Object)) { // If children is not a object is a property from the father
                        // Check if key is not existing
                        let childstoreKey = this.buildPath(parentKey, childKey);
                        retKeys.push(childstoreKey); // Store property on father
                        continue;
                    }
                    //Child is a object
                    if (allowEmpty || Object.keys(child).length > 0) { // If the children has keys store in the objects
                        // Check if key is not existing
                        let childstoreKey = this.buildPath(parentKey, childKey);
                        objects[childstoreKey] = child; // Store in the interation objects
                    }
                }

                if (allowEmpty || Object.keys(tmpCleanParent).length > 0) { // If the father has content
                    let cleanParentKey = parentKey;
                    retKeys.push(cleanParentKey);
                }
            
            }
            if(objects) // If objects is not undefined continue interation
                keys = Object.keys(objects); // Look for the keys again
        }
        return retKeys; // Return clean objects
    }
}
