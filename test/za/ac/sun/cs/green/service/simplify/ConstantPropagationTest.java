package za.ac.sun.cs.green.service.simplify;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.util.Configuration;

public class ConstantPropagationTest {

	public static Green solver;

	@BeforeClass
	public static void initialize() {
		solver = new Green();
		Properties props = new Properties();
		props.setProperty("green.services", "sat");
		props.setProperty("green.service.sat", "(simplify sink)");
		props.setProperty("green.service.sat.sink", "za.ac.sun.cs.green.service.sink.SinkService");
		props.setProperty("green.service.sat.simplify", "za.ac.sun.cs.green.service.simplify.ConstantPropagation");
		Configuration config = new Configuration(solver, props);
		config.configure();
	}

	private void finalCheck(String observed, String[] expected) {
		String s0 = observed.replaceAll("[()]", "");
		String s1 = s0.replaceAll("v[0-9]", "v");
		SortedSet<String> s2 = new TreeSet<String>(Arrays.asList(s1.split("&&")));
		SortedSet<String> s3 = new TreeSet<String>(Arrays.asList(expected));
		assertEquals(s2, s3);
	}

	private void check(Expression expression, String full,
					   String... expected) {
		Instance i = new Instance(solver, null, null, expression);
		Expression e = i.getExpression();
		assertTrue(e.equals(expression));
		assertEquals(expression.toString(), e.toString());
		assertEquals(full, i.getFullExpression().toString());
		Object result = i.request("sat");
		assertNotNull(result);
		assertEquals(Instance.class, result.getClass());
		Instance j = (Instance) result;
		finalCheck(j.getExpression().toString(), expected);
	}

	private void check(Expression expression, Expression parentExpression,
					   String full, String... expected) {
		Instance i1 = new Instance(solver, null, parentExpression);
		Instance i2 = new Instance(solver, i1, expression);
		Expression e = i2.getExpression();
		assertTrue(e.equals(expression));
		assertEquals(expression.toString(), e.toString());
		assertEquals(full, i2.getFullExpression().toString());
		Object result = i2.request("sat");
		assertNotNull(result);
		assertEquals(Instance.class, result.getClass());
		Instance j = (Instance) result;
		finalCheck(j.getExpression().toString(), expected);
	}

	@Test
	public void test01() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);

		Operation first = new Operation(Operation.Operator.ADD, x, y);
		Operation o = new Operation(Operation.Operator.GT, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, o, second);

		check(add, "((x+y)>0)&&(5==x)", "-1*y+-5>0", "1*x+-5==0");
	}

	@Test
	public void test02() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);

		Operation first = new Operation(Operation.Operator.ADD, x, y);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);

		check(add, "(5==x)&&((x+y)==0)", "1*x+-5==0", "1*y+5==0");
	}

	@Test
	public void test03() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c2);
		Operation first = new Operation(Operation.Operator.ADD, x, y);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		check(addF, "((5==x)&&((x+y)==0))&&(z==2)", "1*x+-5==0", "1*y+5==0", "1*z+-2==0");
	}

	@Test
	public void test04() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c2);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		check(addF, "((5==x)&&(((x+y)+z)==0))&&(z==2)", "1*x+-5==0" ,"1*y+7==0", "1*z+-2==0");
	}

	@Test
	public void test05() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c5);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		check(addF, "((5==x)&&(((x+y)+z)==0))&&(z==5)", "1*x+-5==0", "1*y+10==0", "1*z+-5==0");
	}

	@Test
	public void test06() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c5);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		Operation add2 = new Operation(Operation.Operator.ADD, x, y);
		Operation addxy = new Operation(Operation.Operator.AND, addF, add2);

		check(addxy, "(((5==x)&&(((x+y)+z)==0))&&(z==5))&&(x+y)", "1*x+-5==0", "1*y+10==0", "1*y+5", "1*z+-5==0");
	}

	@Test
	public void test07() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c5);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		Operation add2 = new Operation(Operation.Operator.ADD, x, y);
		Operation addxy = new Operation(Operation.Operator.AND, add2, addF);

		check(addxy, "(x+y)&&(((5==x)&&(((x+y)+z)==0))&&(z==5))", "1*x+-5==0", "1*y+10==0", "1*y+5", "1*z+-5==0");
	}

	@Test
	public void test08() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntVariable f = new IntVariable("f", 0, 99);
		IntVariable g = new IntVariable("g", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c5);
		Operation opf = new Operation(Operation.Operator.EQ, f, c5);
		Operation opg = new Operation(Operation.Operator.EQ, z, c2);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		Operation add2 = new Operation(Operation.Operator.ADD, x, y);
		Operation addxy = new Operation(Operation.Operator.AND, add2, addF);
		Operation addxyf = new Operation(Operation.Operator.AND, addxy, opf);
		Operation addxyfg = new Operation(Operation.Operator.AND, opg, addxyf);

		check(addxyf, "((x+y)&&(((5==x)&&(((x+y)+z)==0))&&(z==5)))&&(f==5)", "1*f+-5==0", "1*x+-5==0", "1*y+10==0", "1*y+5", "1*z+-5==0");
	}

	@Test
	public void test09() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntVariable f = new IntVariable("f", 0, 99);
		IntVariable g = new IntVariable("g", 0, 99);
		IntConstant c = new IntConstant(0);
		IntConstant c5 = new IntConstant(5);
		IntConstant c2 = new IntConstant(2);
		Operation opZ = new Operation(Operation.Operator.EQ, z, c5);
		Operation opf = new Operation(Operation.Operator.EQ, f, c5);
		Operation opg = new Operation(Operation.Operator.EQ, g, c2);
		Operation firsta = new Operation(Operation.Operator.ADD, x, y);
		Operation first = new Operation(Operation.Operator.ADD, firsta, z);
		Operation o = new Operation(Operation.Operator.EQ, first, c);
		Operation second = new Operation(Operation.Operator.EQ, c5, x);
		Operation add = new Operation(Operation.Operator.AND, second, o);
		Operation addF = new Operation(Operation.Operator.AND, add, opZ);
		Operation add2 = new Operation(Operation.Operator.ADD, x, y);
		Operation addxy = new Operation(Operation.Operator.AND, add2, addF);
		Operation addxyf = new Operation(Operation.Operator.AND, addxy, opf);
		Operation addxyfg = new Operation(Operation.Operator.AND, addxyf, opg);

		check(addxyfg, "(((x+y)&&(((5==x)&&(((x+y)+z)==0))&&(z==5)))&&(f==5))&&(g==2)", "1*f+-5==0", "1*g+-2==0", "1*x+-5==0", "1*y+10==0", "1*y+5", "1*z+-5==0", "1*y+5");
	}


	//
	@Test
	public void test12() {
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntVariable q = new IntVariable("q", 0, 99);
		IntConstant five = new IntConstant(5);
		IntConstant zero = new IntConstant(0);
		IntConstant three = new IntConstant(3);
		Operation o = new Operation(Operation.Operator.EQ, z, five);
		Operation o1 = new Operation(Operation.Operator.EQ, y, five);
		//Operation oo1 = new Operation(Operation.Operator.EQ, z, three);
		Operation o2 = new Operation(Operation.Operator.LT, new Operation(Operation.Operator.ADD, new Operation(Operation.Operator.ADD, new Operation(Operation.Operator.ADD, z, x), q), y), three);
		Operation o3 = new Operation(Operation.Operator.AND, o, o2);
		Operation o4 = new Operation(Operation.Operator.AND, o3, o1);
		//Operation o5 = new Operation(Operation.Operator.AND,o4,oo1);

		check(o4, "((z==5)&&((((z+x)+q)+y)<3))&&(y==5)", "1*q+1*x+7<0", "1*y+-5==0", "1*z+-5==0");
	}

	//x==1&&y+x<10 -> y+1 < 10
	//x==1&&x+1>0 - > 0==0
	@Test
	public void test13() {
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntVariable q = new IntVariable("q", 0, 99);
		IntConstant five = new IntConstant(5);
		IntConstant zero = new IntConstant(0);
		IntConstant three = new IntConstant(3);
		IntConstant one = new IntConstant(1);

		Operation o1 = new Operation(Operation.Operator.EQ, x, one);
		Operation o2 = new Operation(Operation.Operator.ADD, x, one);
		Operation o3 = new Operation(Operation.Operator.GT, o2, one);
		Operation o4 = new Operation(Operation.Operator.AND, o1, o3);


		check(o4, "(x==1)&&((x+1)>1)", "1*x+-1==0");
	}
	//x==1&&y+x<10 -> y+1 < 10
	@Test
	public void test14() {
		IntVariable y = new IntVariable("y", 0, 99);
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable z = new IntVariable("z", 0, 99);
		IntVariable q = new IntVariable("q", 0, 99);
		IntConstant five = new IntConstant(5);
		IntConstant zero = new IntConstant(0);
		IntConstant three = new IntConstant(3);
		IntConstant one = new IntConstant(1);
		Operation o1 = new Operation(Operation.Operator.EQ, x, one);
		Operation o2 = new Operation(Operation.Operator.ADD, x, y);
		Operation o3 = new Operation(Operation.Operator.LT, o2, new IntConstant(10));
		Operation o4 = new Operation(Operation.Operator.AND, o1, o3);
		check(o4, "(x==1)&&((x+y)<10)", "1*x+-1==0", "1*y+-9<0");
	}


}
