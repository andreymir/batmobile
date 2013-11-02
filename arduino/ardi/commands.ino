char* readCommand() {
  byte commandSize = (byte)btSerial.peek();
  if (commandSize == -1) {
    return NULL; // No command available
  }
  if (btSerial.available() < commandSize + 1) {
    return NULL; // No command available
  }
  btSerial.read(); // remove command size from buffer
  char* command = new char[commandSize + 1];
  for (byte i = 0; i < commandSize; i++) {
    command[i] = (char)btSerial.read();
  }
  command[commandSize] = 0;
  return command;
}
