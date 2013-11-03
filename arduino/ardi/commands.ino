
char* readCommand() {
  int commandSize = btSerial.peek();
  if (commandSize == -1) {
    return NULL; // No command available
  }
  if (btSerial.available() < commandSize + 1) {
    return NULL; // No command available
  }
  
  btSerial.read(); // remove command size from buffer
  char* command = new char[commandSize + 1];
  for (int i = 0; i < commandSize; i++) {
    command[i] = (char)btSerial.read();
  }
  command[commandSize] = 0;
  
  return command;
}

void processCommand(char* command) {
  char c = command[0];
  switch (c) {
    case 'd':
      callDrive(command[1], command[2]);
      break;
    case 'm':
      callChangeMode(command[1]);
      break;
    case 'b':
      march();
      break;
  }
}

void callDrive(char left, char right) {
  Serial.print("callDrive: ");
  Serial.print((int)left);
  Serial.print(", ");
  Serial.println((int)right);
  
  if (mode != Manual && mode != Protected && left != 0 && right != 0) {
    return;
  }
  
  drive(left, right);
}

void callChangeMode(char m) {
  Serial.print("callChangeMode: ");
  Serial.println(m);
  
  switch (m) {
    case 'i':
      setupBot(Idle);
      break;
    case 'm':
      setupBot(Manual);
      break;
    case 'f':
      setupBot(FollowLine);
      break;
    case 's':
      setupBot(Protected);
      break;
  }
}
