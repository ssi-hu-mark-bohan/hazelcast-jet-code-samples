/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package averagesalary.model;

import java.io.Serializable;

import static java.lang.Integer.valueOf;

public class Employee implements Serializable {

    private String id;
    private int salary;

    public Employee() {
    }

    public Employee(String id, int salary) {
        this.id = id;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }

    public void fromString(String value) {
        String[] split = value.split(",");
        if (split.length < 2) {
            throw new IllegalArgumentException(value);
        }
        id = split[0];
        salary = valueOf(split[1]);
    }

    @Override
    public String toString() {
        return "Employee{"
                + "id='" + id + '\''
                + ", salary=" + salary
                + '}';
    }
}