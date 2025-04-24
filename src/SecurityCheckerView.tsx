import { requireNativeView } from 'expo';
import * as React from 'react';

import { SecurityCheckerViewProps } from './SecurityChecker.types';

const NativeView: React.ComponentType<SecurityCheckerViewProps> =
  requireNativeView('SecurityChecker');

export default function SecurityCheckerView(props: SecurityCheckerViewProps) {
  return <NativeView {...props} />;
}
