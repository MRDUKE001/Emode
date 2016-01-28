/*
 * (C) Copyright 2012 ZTE
 * All Rights Reserved
 */

package com.wind.emode;

// Declare the interface.
interface NvRAMAgent {
byte[] readFile(int file_lid);
int writeFile(int file_lid, in byte[] buff);
}