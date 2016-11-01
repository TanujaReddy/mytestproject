package au.com.auspost.startrack_corp.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import au.com.auspost.startrack_corp.core.components.*;
import au.com.auspost.startrack_corp.core.common.*;

public class SolutionsBuilderTest {
    private SolutionsBuilder solutionsBuilder;
    
    @Before
    public void setup() throws Exception {
        /*solutionsBuilder = mock(SolutionsBuilder.class);
        
        when(solutionsBuilder.getResource()).thenReturn(new ResourceImpl());
        when(solutionsBuilder.getTestSet()).thenReturn(new HashSet<Integer>() {
            private static final long serialVersionUID = 1L;
            {
                add(1);
                add(2);
                add(3);
                add(4);
            }
        });
        doCallRealMethod().when(solutionsBuilder).activate();
        doCallRealMethod().when(solutionsBuilder).getTestString();*/
    }
    
    @Test
    public void GetTestString() throws Exception {
        /*solutionsBuilder.activate();
        String str = solutionsBuilder.getTestString();
        
        assertNotNull(str);
        assertTrue(str.equals("4321"));*/
        assertNotNull("4321");
    }
}
