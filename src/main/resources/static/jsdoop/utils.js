function deserializeMultipart(bytes, boundaryBytes, layers, weights) {
    let counter = 0;
    console.log("bytes.length " + bytes.length)
    while (counter <  bytes.length - 1) {
        counter = getNextElement(layers, weights, bytes, boundaryBytes, counter)
        console.log("counter = " + counter)
    }
    console.log("layers = " + layers)
}

function toNpyTensor(uint8array) { 
                const dtypes = {
            "<u1": {
                name: "uint8",
                size: 8,
                arrayConstructor: Uint8Array,
            },
            "|u1": {
                name: "uint8",
                size: 8,
                arrayConstructor: Uint8Array,
            },
            "|i1": {
                name: "int8",
                size: 8,
                arrayConstructor: Int8Array,
            },
            "<u4": {
                name: "uint32",
                size: 32,
                arrayConstructor: Int32Array,
            },
            "<i4": {
                name: "int32",
                size: 32,
                arrayConstructor: Int32Array,
            },
            "<u8": {
                name: "uint64",
                size: 64,
                arrayConstructor: BigUint64Array,
            },
            "<i8": {
                name: "int64",
                size: 64,
                arrayConstructor: BigInt64Array,
            },
            "<f4": {
                name: "float32",
                size: 32,
                arrayConstructor: Float32Array
            },
            "<f8": {
                name: "float64",
                size: 64,
                arrayConstructor: Float64Array
            },
        };
        // https://numpy.org/doc/stable/reference/generated/numpy.lib.format.html#format-version-1-0
        // 6 bytes magic string \x93NUMPY
        // 1 byte major version
        // 1 byte minor version
        // 2 bytes HEADER_LEN
        
        console.log("## ALL = " + new TextDecoder("utf-8").decode(uint8array)  + "###");
        console.log("## INIT = " + new TextDecoder("utf-8").decode(uint8array.subarray(0, 3))  + "###");
        console.log("## NUMPY = " + new TextDecoder("utf-8").decode(uint8array.subarray(3, 9))  + "###");
        console.log("## major version = " + Array.prototype.map.call(uint8array.subarray(9, 10), x => ('00' + x.toString(16)).slice(-2)).join(''));
        console.log("## minor version = " + Array.prototype.map.call(uint8array.subarray(10, 11), x => ('00' + x.toString(16)).slice(-2)).join(''));
        console.log("## header length hex = " + Array.prototype.map.call(uint8array.subarray(11, 13), x => ('00' + x.toString(16)).slice(-2)).join(''));
        console.log("## header length = " + new DataView(uint8array.buffer, uint8array.byteOffset + 11, 2).getUint8(0));
        console.log("## NUMPY = " + Array.prototype.map.call(uint8array.subarray(3, 9), x => ('00' + x.toString(16)).slice(-2)).join(''))
        
        
        const headerLength = new DataView(uint8array.buffer, uint8array.byteOffset + 11, 2).getUint8(0);
        
        console.log("## HEADER = " + headerLength + "###");     
        const hcontents = new TextDecoder("utf-8").decode(uint8array.subarray(13, 13 + headerLength))//72))
        
        console.log("## hcontents " + hcontents + "##")

        let header = JSON.parse(
            hcontents
            .replace(/'/g, '"')
            .replace("False", "false")
            .replace("(", "[")
            .replace(/,*\),*/g, "]")
        );
        console.log(JSON.stringify(header))
        let shape = header.shape;
        let dtype = dtypes[header.descr];
        const from = uint8array.byteOffset + 13 + headerLength
        console.log("from / 4 " + (from / 4.0))
        const to = uint8array.byteOffset + uint8array.byteLength 
        console.log("to / 4 " + (to / 4.0))
        console.log("uint8array.byteOffset " + uint8array.byteOffset)
        console.log("uint8array.byteLength " + uint8array.byteLength)
        console.log("headerLength " + headerLength)
        console.log("headerLength + 13 " + (headerLength + 13))
        console.log("!!!!!!!!!! from = " + uint8array.byteOffset + 13 + headerLength )
        console.log("!!!!!!!!!! to = " + 13 + uint8array.byteOffset + 13 + headerLength + (uint8array.byteLength - 13 + headerLength))   
        console.log("!!!!!!!!!! length " + (to - from))
        console.log("!!!!!!!!!!! 13 + headerLength " + (13 + headerLength))
        const total = (uint8array.byteOffset + uint8array.byteLength) - (uint8array.byteOffset + 13 + headerLength)
        console.log("averrrrrrr " + total)
        let nums = new dtype["arrayConstructor"](
            uint8array.buffer.slice( uint8array.byteOffset + 13 + headerLength, -2 + uint8array.byteOffset + uint8array.byteLength ),//uint8array.buffer.slice(13 + headerLength, 13 + headerLength + uint8array.byteLength),
            0
        );
    
        let npyObject = {
            dtype: dtype.name,
            data: nums,
            shape
        };
        console.log("npyObject = " + JSON.stringify(npyObject))
        const mytensor = tf.tensor(nums, shape, dtypes[header.descr].name)
        console.log("mytensor = " + mytensor)
        return mytensor
}

