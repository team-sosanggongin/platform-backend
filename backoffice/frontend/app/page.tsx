import Link from 'next/link';

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
      <h1 className="text-3xl font-bold text-gray-800">백오피스</h1>
      <p className="mt-2 text-gray-500">관리자 페이지에 오신 것을 환영합니다.</p>
      <Link
        href="/login"
        className="mt-6 rounded-md bg-blue-600 px-6 py-2 text-sm font-medium text-white hover:bg-blue-700"
      >
        로그인
      </Link>
    </div>
  );
}
