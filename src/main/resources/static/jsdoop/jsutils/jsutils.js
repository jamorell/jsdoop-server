function str (tostr) {
  return "" + tostr;
}

function print(toprint) {
  console.debug("FECHA " + new Date() + ": " + ("" + toprint).substring(0, 200) );
}

function len(array) {
  return array.length;
}

function int(toInt) {
  return parseInt(toInt);
}

String.prototype.hashCode = function(){
	var hash = 0;
	if (this.length == 0) return hash;
	for (i = 0; i < this.length; i++) {
		char = this.charCodeAt(i);
		hash = ((hash<<5)-hash)+char;
		hash = hash & hash; // Convert to 32bit integer
	}
	return hash;
}

Array.prototype.append = function (){
    //....
    return Array.prototype.push.apply(this,arguments);
}





class JSDTime {
  static time () {
    return new Date().getMilliseconds() * 1000;
  }
}
const time = JSDTime;

function round (number) {
  return Math.round(number)
}

function range(number) {
  return _.range(number);
}

/** //https://stackoverflow.com/questions/11935175/sampling-a-random-subset-from-an-array
function getRandomSubarray(arr, size) {
    var shuffled = arr.slice(0), i = arr.length, temp, index;
    while (i--) {
        index = Math.floor((i + 1) * Math.random());
        temp = shuffled[index];
        shuffled[index] = shuffled[i];
        shuffled[i] = temp;
    }
    return shuffled.slice(0, size);
}
**/


const random = Math.random;
Math.random.sample = function (arr, size) {
    var shuffled = arr.slice(0), i = arr.length, min = i - size, temp, index;
    while (i-- > min) {
        index = Math.floor((i + 1) * Math.random());
        temp = shuffled[index];
        shuffled[index] = shuffled[i];
        shuffled[i] = temp;
    }
    return shuffled.slice(min);
}
Math.random.randint = function (min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}







//const False = false;
//const True = true;
//const None = null;

if (typeof Array.isArray === 'undefined') {
  Array.isArray = function(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]';
  }
};
