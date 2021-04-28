package com.example.audiotracks;

import junit.framework.TestCase;

public class ProjectTest extends TestCase {

    public void testAddPath() {
        Project proj = new Project();
        proj.addPath(" ", 0);
        assertTrue(proj.paths[0] != null);
    }

    public void testRemovePath() {
        Project proj = new Project();
        proj.addPath(" ", 0);
        proj.removePath(0);
        assertTrue(proj.paths[0 ]  == null);
    }

    public void testGetPath() {
        Project proj = new Project();
        proj.addPath("teststring", 0);
        assertEquals("teststring", proj.getPath(0));
    }

    public void testTestSetName() {
        Project proj = new Project();
        proj.setName("chicken");
        assertEquals("chicken", proj.name);

    }

    public void testTestGetName() {
        Project proj = new Project();
        proj.setName("chicken");
        assertEquals("chicken", proj.getName());

    }

    public void testSetPaths() {
        Project proj = new Project();
        String array [] = {};
        proj.setPaths(array);
        assertEquals(proj.paths, array);

    }

    public void testGetPaths() {
        Project proj = new Project();
        String array [] = {};
        proj.setPaths(array);
        assertEquals(proj.paths, proj.getPaths());
    }
}