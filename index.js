import { NativeEventEmitter, NativeModules, Platform } from 'react-native';
const { RNImmersive } = NativeModules;

const unSupportedError = __DEV__
  ? () => { throw new Error('[react-native-immersive] should not be called on iOS') }
  : () => {};

const emitter = new NativeEventEmitter(RNImmersive);

const Immersive = Platform.OS === 'android' ? {
  on: () => RNImmersive.setImmersive(true),
  off: () => RNImmersive.setImmersive(false),
  setImmersive: (isOn) => RNImmersive.setImmersive(isOn),
  getImmersive: () => RNImmersive.getImmersive(), // does not always match actual display state
  addImmersiveListener: (listener) => emitter.addListener('@@IMMERSIVE_STATE_CHANGED', listener),
} : {
  on: unSupportedError,
  off: unSupportedError,
  setImmersive: unSupportedError,
  getImmersive: unSupportedError,
  addImmersiveListener: unSupportedError,
};

export { Immersive };
export default Immersive;
