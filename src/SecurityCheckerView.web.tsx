import * as React from 'react';

import { SecurityCheckerViewProps } from './SecurityChecker.types';

export default function SecurityCheckerView(props: SecurityCheckerViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
