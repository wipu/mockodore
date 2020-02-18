package org.fluentjava.mockodore.app.sidex;

public interface SysexEventListener {

	SysexEventListener NONE = new SysexEventListener() {

		@Override
		public void sysexSkipByteEnd() {
			// nothing to do
		}

		@Override
		public void sysexSkipByteBegin() {
			// nothing to do
		}

		@Override
		public void sysexReceiveModeBegin() {
			// nothing to do
		}

		@Override
		public void sysexReceiveByteEnd() {
			// nothing to do
		}

		@Override
		public void sysexReceiveByteBegin() {
			// nothing to do
		}

		@Override
		public void sysexReceiveModeEnd() {
			// nothing to do
		}

	};

	void sysexSkipByteBegin();

	void sysexSkipByteEnd();

	void sysexReceiveModeBegin();

	void sysexReceiveByteBegin();

	void sysexReceiveByteEnd();

	void sysexReceiveModeEnd();

}
