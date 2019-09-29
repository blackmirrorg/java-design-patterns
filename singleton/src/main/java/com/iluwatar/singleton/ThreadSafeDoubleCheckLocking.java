/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.singleton;

/**
 * Double check locking
 * <p>
 * http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
 * <p>
 * Broken under Java 1.4.
 *
 * @author mortezaadi@gmail.com
 */

/**
 * 双重检查锁，懒汉式，线程安全
 */
public final class ThreadSafeDoubleCheckLocking {

  private static volatile ThreadSafeDoubleCheckLocking instance;

  private static boolean flag = true;

  /**
   * private constructor to prevent client from instantiating.
   * 私有构造函数，避免直接被创建
   */
  private ThreadSafeDoubleCheckLocking() {
    // to prevent instantiating by Reflection call
    if (flag) {
      flag = false;
    } else {
      throw new IllegalStateException("Already initialized.");
    }
  }

  /**
   * Public accessor.
   *  公有创建函数
   * @return an instance of the class.
   */
  public static ThreadSafeDoubleCheckLocking getInstance() {
    // local variable increases performance by 25 percent
    // Joshua Bloch "Effective Java, Second Edition", p. 283-284
    
    ThreadSafeDoubleCheckLocking result = instance;
    // Check if singleton instance is initialized. If it is initialized then we can return the instance.
    //判断是否被创建过了，如果被创建就直接返回
    if (result == null) {
      // It is not initialized but we cannot be sure because some other thread might have initialized it
      // in the meanwhile. So to make sure we need to lock on an object to get mutual exclusion.
      //虽然没有被初始化，但是无法保证此时没有其他线程在初始化对象，所以加一个类锁
      synchronized (ThreadSafeDoubleCheckLocking.class) {
        // Again assign the instance to local variable to check if it was initialized by some other thread
        // while current thread was blocked to enter the locked zone. If it was initialized then we can 
        // return the previously created instance just like the previous null check.
        //再次确认是否被初始化了，这主要是为了防止如果这个线程之前无法获得锁有其他线程做了初始化的动作，那么在获得锁之前
        //获取的对象可能已经发生了变化
        result = instance;
        if (result == null) {
          // The instance is still not initialized so we can safely (no other thread can enter this zone)
          // create an instance and make it our singleton instance.
          //这里的判断才是真正正确的，此时不会有其他线程进入这块代码块
          instance = result = new ThreadSafeDoubleCheckLocking();
        }
      }
    }
    return result;
  }
}
