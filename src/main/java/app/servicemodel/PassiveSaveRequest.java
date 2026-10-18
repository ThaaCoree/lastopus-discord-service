package app.servicemodel;

import java.util.List;

public record PassiveSaveRequest(String name, List<Integer> added, List<Integer> removed) {}