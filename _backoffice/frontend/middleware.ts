import { NextRequest, NextResponse } from 'next/server';

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  const isAuthPage = (pathname.startsWith('/login') &&
                   !pathname.startsWith('/login/change-password')) ||
                   pathname.startsWith('/locked') ||
                   pathname.startsWith('/verify');

  const sessionCookie = request.cookies.get('JSESSIONID');

  if (!isAuthPage && !sessionCookie) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  if (isAuthPage && sessionCookie) {
    return NextResponse.redirect(new URL('/backoffice', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/((?!_next/static|_next/image|favicon.ico).*)'],
};