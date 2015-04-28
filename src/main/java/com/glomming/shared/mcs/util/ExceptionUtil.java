package com.glomming.shared.mcs.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionUtil {
  /**
   * This attempts to generate a 'reasonable' stack-trace.
   * It trims the stack-trace up to the originating class, so that we don't see a huge list
   * of calls from the web-container or some other deep library that are not informative at all.
   *
   * @param originatingClass
   * @return String containing the trimmed stack-trace.
   */
  public static String getTrimmedStackTrace(Class<?> originatingClass, Exception exception) {
    String[] frames = ExceptionUtils.getStackFrames(exception);
    StringBuilder stackTraceBuffer = new StringBuilder();
    int foundIndex = -1;
    for (int i = frames.length; --i >= 0; ) {
      if (frames[i].contains(originatingClass.getName())) {
        foundIndex = i;
        break;
      }
    }
    if (foundIndex >= 0) {
      for (int i = 0; i <= foundIndex; i++) {
        stackTraceBuffer.append(frames[i]).append("\n");
      }
      return stackTraceBuffer.toString();
    }
    // Originating class is not found, return the default one.
    return ExceptionUtils.getStackTrace(exception);
  }

  /**
   * This attempts to generate a 'reasonable' stack-trace.
   * It trims the stack-trace up to the originating class, so that we don't see a huge list
   * of calls from the web-container or some other deep library that are not informative at all.
   *
   * @param originatingClass
   * @return String[] containing the trimmed stack-trace.
   */
  public static String[] getTrimmedStackArray(Class<?> originatingClass, Exception exception) {
    String[] frames = ExceptionUtils.getStackFrames(exception);
    int foundIndex = -1;
    for (int i = frames.length; --i >= 0; ) {
      if (frames[i].contains(originatingClass.getName())) {
        foundIndex = i;
        break;
      }
    }
    if (foundIndex >= 0) {
      String[] array = new String[foundIndex + 1];
      for (int i = 0; i <= foundIndex; i++)
        array[i] = frames[i];
      return array;
    }
    // Originating class is not found, return the default one.
    return frames;
  }
}


